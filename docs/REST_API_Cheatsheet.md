# Шпаргалка по REST API Elasticsearch / OpenSearch

Этот документ содержит основные команды для управления кластером, индексами, документами и поиском через REST API. Подходит как для Elasticsearch, так и для OpenSearch (эндпоинты совместимы).

## Содержание
- [Проверка кластера и узлов](#проверка-кластера-и-узлов)
- [Управление индексами](#управление-индексами)
- [CRUD с документами](#crud-с-документами)
- [Пакетные операции (_bulk)](#пакетные-операции-_bulk)
- [Массовое обновление и удаление по запросу](#массовое-обновление-и-удаление-по-запросу)
- [Поиск (Query DSL)](#поиск-query-dsl)
- [Алиасы](#алиасы)
- [Принудительное обновление видимости (_refresh)](#принудительное-обновление-видимости-_refresh)
- [Данные учётной записи (Security API)](#данные-учётной-записи-security-api)
- [Дополнительные полезные команды](#дополнительные-полезные-команды)

---

## Проверка кластера и узлов

```bash
# Состояние кластера
curl -s "localhost:9200/_cat/health?v"

# Список узлов
curl -s "localhost:9200/_cat/nodes?v"

# Детальная статистика всех узлов
curl -s "localhost:9200/_nodes/stats?pretty"

# Статистика конкретного узла (например, data-1)
curl -s "localhost:9200/_nodes/data-1/stats?pretty"

# Роли конкретного узла
curl -s "localhost:9200/_nodes/data-1?filter_path=nodes.*.roles&pretty"
```

## Управление индексами
```bash
# Создание индекса с настройками и маппингом
curl -X PUT "localhost:9200/products" -H 'Content-Type: application/json' -d'
{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 0
  },
  "mappings": {
    "properties": {
      "title": { "type": "text" },
      "description": { "type": "text" },
      "price": { "type": "float" },
      "category": { "type": "keyword" },
      "created_at": { "type": "date" }
    }
  }
}'

# Получение информации об индексе
curl -X GET "localhost:9200/products?pretty"

# Удаление индекса
curl -X DELETE "localhost:9200/products"
```

## CRUD с документами
```bash
# Индексация документа с явным ID
curl -X PUT "localhost:9200/products/_doc/1" -H 'Content-Type: application/json' -d'
{
  "title": "iPhone 13",
  "description": "Apple smartphone",
  "price": 799.99,
  "category": "electronics",
  "created_at": "2024-01-15T10:00:00"
}'

# Индексация с автоматической генерацией ID (POST)
curl -X POST "localhost:9200/products/_doc" -H 'Content-Type: application/json' -d'
{
  "title": "USB Cable",
  "price": 9.99
}'

# Чтение документа по ID
curl -X GET "localhost:9200/products/_doc/1?pretty"

# Частичное обновление (_update)
curl -X POST "localhost:9200/products/_update/1" -H 'Content-Type: application/json' -d'
{
  "doc": {
    "price": 749.99
  }
}'

# Удаление документа
curl -X DELETE "localhost:9200/products/_doc/1"
```

## Пакетные операции (_bulk)
```bash
# Массовая загрузка нескольких документов
curl -X POST "localhost:9200/_bulk" -H 'Content-Type: application/json' --data-binary @- <<EOF
{ "index": { "_index": "products", "_id": "2" } }
{ "title": "Samsung Galaxy S23", "description": "Flagship Android phone", "price": 999.99, "category": "electronics", "created_at": "2024-02-01T09:00:00" }
{ "index": { "_index": "products", "_id": "3" } }
{ "title": "MacBook Pro", "description": "Apple laptop with M3 chip", "price": 1999.99, "category": "computers", "created_at": "2024-01-20T14:30:00" }
{ "index": { "_index": "products", "_id": "4" } }
{ "title": "Dell XPS 13", "description": "Windows ultrabook", "price": 1299.99, "category": "computers", "created_at": "2024-02-10T11:15:00" }
EOF
```

## Массовое обновление и удаление по запросу
```bash
# Обновить все документы с category = computers (увеличить цену на 10%)
curl -X POST "localhost:9200/products/_update_by_query?pretty" -H 'Content-Type: application/json' -d'
{
  "script": {
    "source": "ctx._source.price = ctx._source.price * 1.1",
    "lang": "painless"
  },
  "query": {
    "term": { "category": "computers" }
  }
}'

# Удалить все документы с category = electronics
curl -X POST "localhost:9200/products/_delete_by_query?pretty" -H 'Content-Type: application/json' -d'
{
  "query": {
    "term": { "category": "electronics" }
  }
}'
```

## Поиск (Query DSL)
```bash
# Получить все документы (match_all)
curl -X GET "localhost:9200/products/_search?pretty" -H 'Content-Type: application/json' -d'
{
  "query": {
    "match_all": {}
  }
}'

# Полнотекстовый поиск по полю description (match)
curl -X GET "localhost:9200/products/_search?pretty" -H 'Content-Type: application/json' -d'
{
  "query": {
    "match": {
      "description": "Apple laptop"
    }
  }
}'

# Точное совпадение по keyword-полю (term)
curl -X GET "localhost:9200/products/_search?pretty" -H 'Content-Type: application/json' -d'
{
  "query": {
    "term": {
      "category": "computers"
    }
  }
}'

# Комбинированный запрос с bool (must + filter)
curl -X GET "localhost:9200/products/_search?pretty" -H 'Content-Type: application/json' -d'
{
  "query": {
    "bool": {
      "must": {
        "match": { "description": "laptop" }
      },
      "filter": {
        "range": { "price": { "lte": 2000 } }
      }
    }
  }
}'
```

## Алиасы
```bash
# Создать алиас, указывающий на индекс products
curl -X POST "localhost:9200/_aliases" -H 'Content-Type: application/json' -d'
{
  "actions": [
    { "add": { "index": "products", "alias": "catalog" } }
  ]
}'

# Атомарное переключение алиаса на новый индекс (v2)
curl -X POST "localhost:9200/_aliases" -H 'Content-Type: application/json' -d'
{
  "actions": [
    { "remove": { "index": "products", "alias": "catalog" } },
    { "add": { "index": "products_v2", "alias": "catalog" } }
  ]
}'

# Поиск через алиас
curl -X GET "localhost:9200/catalog/_search?pretty" -H 'Content-Type: application/json' -d'
{
  "query": { "match_all": {} }
}'
```

## Принудительное обновление видимости (_refresh)
```bash
# Немедленно сделать все изменения видимыми для поиска
curl -X POST "localhost:9200/products/_refresh"
```

## Данные учётной записи (Security API)
```bash
# Получить информацию о текущем аутентифицированном пользователе
curl -X GET "localhost:9200/_plugins/_security/api/account?pretty" -u admin:admin
```

## Дополнительные полезные команды
```bash
# Список всех индексов
curl -s "localhost:9200/_cat/indices?v"

# Просмотр маппинга индекса
curl -X GET "localhost:9200/products/_mapping?pretty"

# Просмотр настроек индекса
curl -X GET "localhost:9200/products/_settings?pretty"

# Проверка существования документа
curl -I "localhost:9200/products/_doc/1"
```

Примечание: для OpenSearch эндпоинты идентичны, за исключением API безопасности, который использует префикс _plugins/_security/.