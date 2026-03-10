# java-filmorate
![alt text](ER.jpg)

Примеры запросов:

```sql
SELECT f.*, m.name AS mpa_name, m.description AS mpa_description
FROM films f
LEFT JOIN mpa m ON f.mpa_id = m.mpa_id
WHERE f.id = 1;
```

Template repository for Filmorate project.
