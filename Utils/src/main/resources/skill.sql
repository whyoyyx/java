1、排重(oracle)
SELECT T.*
FROM (
        SELECT A,B,C,D,(ROW_NUMBER() OVER (PARTITION BY A DESC )) AS F
      ) T
WHERE T.F <= 1