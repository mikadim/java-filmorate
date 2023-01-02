package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.controller.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.FilmStorageError;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component("dbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ConversionService conversionService;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, ConversionService conversionService) {
        this.jdbcTemplate = jdbcTemplate;
        this.conversionService = conversionService;
    }

    @Override
    public Film addFilm(FilmRequestDto dto) {
        String queryFilm = "insert into FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) values (?, ?, ?, ?, ?)";
        KeyHolder key = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(queryFilm, new String[]{"id"});
            stmt.setString(1, dto.getName());
            stmt.setString(2, dto.getDescription());
            stmt.setDate(3, Date.valueOf(dto.getReleaseDate()));
            stmt.setInt(4, dto.getDuration());
            stmt.setInt(5, dto.getMpa().getId());
            return stmt;
        }, key);
        Integer filmId = key.getKeyAs(Integer.class);
        String queryFilmGenre = "insert into FILM_GENRE (ID_FILM, ID_GENRE) values (?, ?)";
        Optional.ofNullable(dto.getGenres()).ifPresent(g -> {
            for (GenreDto genre : g) {
                jdbcTemplate.update(queryFilmGenre,
                        filmId,
                        genre.getId());
            }
        });
        String queryFilmLikes = "insert into LIKES (ID_FILM, ID_USER) values (?, ?)";
        Optional.ofNullable(dto.getLikes()).ifPresent(g -> {
            for (Integer l : g) {
                jdbcTemplate.update(queryFilmLikes,
                        filmId,
                        l);
            }
        });
        return getFilm(filmId);
    }

    @Override
    public Film updateFilm(FilmRequestDto dto) {
        int idFilm = dto.getId();
        String query = "update FILMS set NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, MPA_ID = ? where ID = ?";
        int update = jdbcTemplate.update(query
                , dto.getName()
                , dto.getDescription()
                , dto.getReleaseDate()
                , dto.getDuration()
                , dto.getMpa().getId()
                , idFilm);
        if (update > 0) {

            String checkGenres = "select ID_GENRE from FILM_GENRE where ID_FILM = ?";
            List<Integer> filmGenres = jdbcTemplate.query(checkGenres, (r, n) -> r.getInt("ID_GENRE"), idFilm);
            Optional.ofNullable(dto.getGenres()).ifPresentOrElse(g -> {
                        Set<Integer> genresId = g.stream().map(genre -> genre.getId()).collect(Collectors.toSet());
                        for (Integer genreId : genresId) {
                            if (!filmGenres.contains(genreId)) {
                                String queryFilmGenre = "insert into FILM_GENRE (ID_FILM, ID_GENRE) values (?, ?)";
                                jdbcTemplate.update(queryFilmGenre, idFilm, genreId);
                            } else {
                                filmGenres.remove(genreId);
                            }
                        }
                        for (Integer i : filmGenres) {
                            String queryDelFilmGenre = "delete from FILM_GENRE where ID_FILM = ? and ID_GENRE = ?";
                            jdbcTemplate.update(queryDelFilmGenre, idFilm, i);
                        }
                    }, () -> {
                        String queryDelFilmGenres = "delete from FILM_GENRE where ID_FILM = ?";
                        jdbcTemplate.update(queryDelFilmGenres, idFilm);
                    }
            );
            return getFilm(idFilm);
        }
        throw new FilmStorageError("Фильм для обновления не найден");
    }

    @Override
    public void deleteFilm(Integer id) {
        String query = "delete from FILMS where ID = ?";
        jdbcTemplate.update(query, id);
    }

    @Override
    public List<Film> getFilms() {
        String query = "select f.*, m.NAME as MPA_NAME, (select listagg(ID_GENRE, ',') from FILM_GENRE " +
                "where ID_FILM = f.ID) as GENRE, (select listagg(ID_USER, ',') from LIKES where ID_FILM = f.ID) as LIKES " +
                "from FILMS f left join MPA m on m.ID = f.MPA_ID";
        return jdbcTemplate.query(query, this::mapRowToFilm);
    }

    @Override
    public Film getFilm(Integer id) {
        String query = "select f.*, m.NAME as MPA_NAME, (select listagg(ID_GENRE, ',') from FILM_GENRE " +
                "where ID_FILM = f.ID) as GENRE, (select listagg(ID_USER, ',') from LIKES where ID_FILM = f.ID) as LIKES " +
                "from FILMS f left join MPA m on m.ID = f.MPA_ID where f.ID = ?";
        List<Film> list = jdbcTemplate.query(query, this::mapRowToFilm, id);
        if (list.size() > 0) {
            return list.get(0);
        }
        throw new FilmStorageError("Фильм не найден");
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        String check = "select COUNT(*) as TOTAL from LIKES where ID_FILM = ? AND ID_USER = ?";
        Integer total = jdbcTemplate.queryForObject(check, (r, i) -> r.getInt("TOTAL"), id, userId);
        if (total == 0) {
            String query = "insert into LIKES (ID_FILM, ID_USER) values (?, ?)";
            jdbcTemplate.update(query, id, userId);
        }
    }

    @Override
    public void removeLike(Integer id, Integer userId) {
        String query = "delete from LIKES where ID_FILM = ? AND ID_USER = ?";
        jdbcTemplate.update(query, id, userId);
    }

    @Override
    public void deleteAllFilms() {
        jdbcTemplate.update("delete from FILMS");
        jdbcTemplate.execute("alter table FILMS alter column ID RESTART WITH 1");
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(resultSet.getInt("ID"));
        film.setName(resultSet.getString("NAME"));
        film.setDescription(resultSet.getString("DESCRIPTION"));
        film.setDuration(resultSet.getInt("DURATION"));
        Optional.ofNullable(resultSet.getDate("RELEASE_DATE")).ifPresent(d -> film.setReleaseDate(d.toLocalDate()));
        Optional.ofNullable(resultSet.getInt("MPA_ID")).ifPresent(m -> film.setMpa(new Mpa(m)));
        Optional.ofNullable(resultSet.getString("MPA_NAME")).ifPresent(n -> film.getMpa().setName(n));
        Optional.ofNullable(resultSet.getString("GENRE"))
                .ifPresent(s -> film.setGenres(Arrays.stream(s.split(","))
                        .map(Integer::valueOf)
                        .map(i -> {
                                    String genreName = "select NAME from GENRES where ID = ?";
                                    String name = jdbcTemplate.queryForObject(genreName, (r, n) -> r.getString("NAME"), i);
                                    return new Genre(i, name);
                                }
                        )
                        .collect(Collectors.toList())));
        Optional.ofNullable(resultSet.getString("LIKES"))
                .ifPresent(s -> film.setLikes(Arrays.stream(s.split(","))
                        .map(Integer::valueOf)
                        .collect(Collectors.toSet())));
        return film;
    }
}
