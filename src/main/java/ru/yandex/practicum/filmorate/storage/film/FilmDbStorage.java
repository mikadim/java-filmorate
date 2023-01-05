package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.core.convert.ConversionService;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.controller.dto.FilmRequestDto;
import ru.yandex.practicum.filmorate.controller.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.FilmStorageException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
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

        Optional.ofNullable(dto.getGenres()).ifPresent(g -> {
            String query = "insert into FILM_GENRE (ID_FILM, ID_GENRE) values (?, ?)";
            batchUpdateFilmIdPlusAnotherId(g.stream().map((genre) -> List.of(filmId, genre.getId()))
                    .collect(Collectors.toList()), query);
        });

        Optional.ofNullable(dto.getLikes()).ifPresent(g -> {
            String query = "insert into LIKES (ID_FILM, ID_USER) values (?, ?)";
            batchUpdateFilmIdPlusAnotherId(g.stream().map((likeId) -> List.of(filmId, likeId))
                    .collect(Collectors.toList()), query);
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
            final List<Integer> filmGenres = jdbcTemplate.query(checkGenres, (r, n) -> r.getInt("ID_GENRE"), idFilm);

            Optional.ofNullable(dto.getGenres()).ifPresentOrElse(g -> {
                        List<Integer> filmGenresForInsert = g.stream()
                                .map(genre -> genre.getId())
                                .distinct()
                                .filter((id) -> !filmGenres.contains(id))
                                .collect(Collectors.toList());
                        List<Integer> filmGenresForDelete = filmGenres.stream()
                                .filter(element -> {
                                    for (GenreDto dt : g) {
                                        if (element == dt.getId()) {
                                            return false;
                                        }
                                    }
                                    return true;
                                })
                                .collect(Collectors.toList());
                        String queryAddFilmGenre = "insert into FILM_GENRE (ID_FILM, ID_GENRE) values (?, ?)";
                        batchUpdateFilmIdPlusAnotherId(filmGenresForInsert.stream().map((val) -> List.of(idFilm, val))
                                .collect(Collectors.toList()), queryAddFilmGenre);
                        String queryDeleteFilmGenre = "delete from FILM_GENRE where ID_FILM = ? and ID_GENRE = ?";
                        batchUpdateFilmIdPlusAnotherId(filmGenresForDelete.stream().map((val) -> List.of(idFilm, val))
                                .collect(Collectors.toList()), queryDeleteFilmGenre);
                    }, () -> {
                        String queryDelFilmGenres = "delete from FILM_GENRE where ID_FILM = ?";
                        jdbcTemplate.update(queryDelFilmGenres, idFilm);
                    }
            );
            return getFilm(idFilm);
        }
        throw new FilmStorageException("Фильм для обновления не найден");
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
        throw new FilmStorageException("Фильм не найден");
    }

    @Override
    public void addLike(Integer id, Integer userId) {
        String check = "select ID from FILMS where ID = ? " +
                "union all " +
                "select ID from USERS where ID = ? " +
                "union all " +
                "select ID_FILM from LIKES where ID_FILM = ? AND ID_USER = ?";
        List<Integer> list = jdbcTemplate.query(check, (r, i) -> r.getInt("ID"), id, userId, id, userId);
        if (list.size() == 2) {
            String query = "insert into LIKES (ID_FILM, ID_USER) values (?, ?)";
            jdbcTemplate.update(query, id, userId);
        } else if (list.size() < 2) {
            throw new FilmStorageException("Фильм или пользователь не найден");
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

    private void batchUpdateFilmIdPlusAnotherId(final List<List<Integer>> rowForAdd, final String query) {
        jdbcTemplate.batchUpdate(query,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        List<Integer> row = rowForAdd.get(i);
                        ps.setInt(1, row.get(0));
                        ps.setInt(2, row.get(1));
                    }

                    @Override
                    public int getBatchSize() {
                        return rowForAdd.size();
                    }
                });
    }
}
