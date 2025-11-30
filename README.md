# elasticsearch-demo

1. Развернуть Elasticsearch и Kibana
```bash
docker-compose up
```

### Проверка Elasticsearch 
1. Проверка кластера
```bash
curl localhost:9200
```

2. Получение всех записей
```bash
curl http://localhost:8080/api/persons
```

### Поисковые запросы
1. Поиск по имени:
```bash
curl "http://localhost:8080/api/persons/search/first-name?firstName=John"
```

2. Поиск по фамилии:
```bash
curl "http://localhost:8080/api/persons/search/last-name?lastName=Smith"
```

3. Поиск по городу:
```bash
curl "http://localhost:8080/api/persons/search/city?city=New%20York"
```

4. Поиск по возрасту:
```bash
curl "http://localhost:8080/api/persons/search/age-range?min=25&max=35"
```

5. Полнотекстовый поиск в bio:
```bash
curl "http://localhost:8080/api/persons/search/bio?text=Java"
```

6. Глобальный поиск:
```bash
curl "http://localhost:8080/api/persons/search/global?q=developer"
```

7. Комбинированный поиск:
```bash
curl "http://localhost:8080/api/persons/search/city-age?city=San%20Francisco&age=30"
```

8. Создание новой персоны
```bash
curl -X POST http://localhost:8080/api/persons \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Ivan",
    "lastName": "Petrov",
    "age": 28,
    "profession": "Engineer",
    "city": "Moscow",
    "bio": "Software engineer working with Java and Elasticsearch"
  }'
```

### Kibana

1. Kibana http://localhost:5601

### References 
`1.` Запуск Elasticsearch и Kibana в Docker https://dockerhosting.ru/blog/zapusk-elasticsearch-i-kibana-v-docker/  
`2.` ElasticSearch: Всё, что нужно знать за 30 минут https://youtu.be/vxE1aGTEnbE?si=3aciENjJcyrH4vD8  
`3.` Используем Elasticsearch вместе со Spring Boot (docker-compose.yml, PostgreSQL, Elasticsearch, Kibana, Logstash) https://habr.com/ru/articles/766674/  