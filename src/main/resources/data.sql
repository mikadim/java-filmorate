INSERT INTO MPA (NAME)
(SELECT const
FROM (SELECT 1 as posl, 'G' as const
      UNION
      SELECT 2, 'PG'
      UNION
      SELECT 3, 'PG-13'
      UNION
      SELECT 4, 'R'
      UNION
      SELECT 5, 'NC-17'
      ORDER BY posl ASC
      )
WHERE const NOT IN (SELECT NAME FROM MPA ORDER BY NAME ASC));

INSERT INTO GENRES (NAME)
(SELECT const
FROM (SELECT 1 as posl, 'Комедия' as const
      UNION
      SELECT 2, 'Драма'
      UNION
      SELECT 3, 'Мультфильм'
      UNION
      SELECT 4, 'Триллер'
      UNION
      SELECT 5, 'Документальный'
      UNION
      SELECT 6, 'Боевик'
      ORDER BY posl ASC)
WHERE const NOT IN (SELECT NAME FROM GENRES));

