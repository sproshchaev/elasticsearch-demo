# Elasticsearch Mapping Cheatsheet

Шпаргалка по управлению маппингом, типам данных и основным операциям в Elasticsearch/OpenSearch.  
Основана на материалах вебинара «Моделирование данных: сопоставления (mappings) и типы данных».

---

## Базовые операции с индексами и маппингом

### Создание индекса с явным маппингом
```json
PUT /my_index
{
  "mappings": {
    "properties": {
      "title":    { "type": "text" },
      "tags":     { "type": "keyword" },
      "price":    { "type": "float" },
      "quantity": { "type": "integer" },
      "created":  { "type": "date", "format": "yyyy-MM-dd HH:mm:ss||epoch_millis" },
      "is_active":{ "type": "boolean" }
    }
  }
}
```

## Просмотр маппинга индекса
```json
GET /my_index/_mapping
```

## Добавление нового поля в существующий индекс
```json
PUT /my_index/_mapping
{
  "properties": {
    "description": { "type": "text" }
  }
}
```

## Удаление индекса
```json
DELETE /my_index
```

## Работа с документами

## Индексация документа
```json
POST /my_index/_doc/1
{
  "title": "Elasticsearch Guide",
  "tags": ["elasticsearch", "search"],
  "price": 29.99,
  "quantity": 100,
  "created": "2025-01-15 10:30:00",
  "is_active": true
}
```

## Получение документа по ID
```json
GET /my_index/_doc/1
```

## Обновление документа (частичное)
```json
DELETE /my_index/_doc/1
```

## Поисковые запросы

## Полнотекстовый поиск по полю text
```json
GET /my_index/_search
{
  "query": {
    "match": { "title": "guide" }
  }
}
```

## Точное совпадение по полю keyword
```json
GET /my_index/_search
{
  "query": {
    "term": { "tags": "elasticsearch" }
  }
}
```

## Поиск по диапазону дат
```json
GET /my_index/_search
{
  "query": {
    "range": {
      "created": { "gte": "2025-01-01", "lte": "2025-01-31" }
    }
  }
}
```

## Поиск по нескольким условиям (bool запрос)
```json
GET /my_index/_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "title": "elasticsearch" } }
      ],
      "filter": [
        { "term": { "is_active": true } },
        { "range": { "price": { "lte": 30 } } }
      ]
    }
  }
}
```

## Агрегация по полю keyword
```json
GET /my_index/_search
{
  "size": 0,
  "aggs": {
    "popular_tags": {
      "terms": { "field": "tags", "size": 10 }
    }
  }
}
```

## Управление динамическим маппингом

## Строгий режим (strict) – отклонять документы с неизвестными полями
```json
PUT /strict_index
{
  "mappings": {
    "dynamic": "strict",
    "properties": {
      "name": { "type": "text" }
    }
  }
}
```

## Игнорирование новых полей (false) – не индексировать, но хранить в _source
```json
PUT /ignore_index
{
  "mappings": {
    "dynamic": false,
    "properties": {
      "name": { "type": "text" }
    }
  }
}
```

## Включение динамического маппинга (true) – значение по умолчанию
```json
PUT /dynamic_index
{
  "mappings": {
    "dynamic": true
  }
}
```

## Динамические шаблоны (Dynamic Templates)

## Пример: строки, начинающиеся с text_, становятся text; заканчивающиеся на _kw — keyword
```json
PUT /templates_index
{
  "mappings": {
    "dynamic_templates": [
      {
        "strings_as_text": {
          "match_mapping_type": "string",
          "match": "text_*",
          "mapping": { "type": "text" }
        }
      },
      {
        "strings_as_keyword": {
          "match_mapping_type": "string",
          "match": "*_kw",
          "mapping": { "type": "keyword" }
        }
      }
    ]
  }
}
```

## Специальные параметры полей

## coerce – принудительное приведение типов
```json
PUT /coerce_index
{
  "settings": {
    "index.mapping.coerce": false
  },
  "mappings": {
    "properties": {
      "age": {
        "type": "integer",
        "coerce": true
      }
    }
  }
}
```

## doc_values – отключение для экономии места (если не нужны агрегации/сортировка)
```json
PUT /no_doc_values
{
  "mappings": {
    "properties": {
      "session_id": {
        "type": "keyword",
        "doc_values": false
      }
    }
  }
}
```

## index_options – управление детализацией инвертированного индекса
```json
PUT /index_options_demo
{
  "mappings": {
    "properties": {
      "content": {
        "type": "text",
        "index_options": "offsets"
      }
    }
  }
}
```

## Reindex API (изменение типа поля или маппинга)

## 1. Создать новый индекс с правильным маппингом
```json
PUT /new_index
{
  "mappings": {
    "properties": {
      "price": { "type": "float" }
    }
  }
}
```

## 2. Перенести данные из старого индекса
```json
POST /_reindex
{
  "source": { "index": "old_index" },
  "dest": { "index": "new_index" }
}
```

## 3. (Опционально) Использовать скрипт для преобразования данных
```json
POST /_reindex
{
  "source": { "index": "old_index" },
  "dest": { "index": "new_index" },
  "script": {
    "source": "ctx._source.price = ctx._source.price.toString()"
  }
}
```

## Шаблоны индексов и компонентные шаблоны

## Компонентный шаблон с общими настройками
```json
PUT _component_template/common_settings
{
  "template": {
    "settings": {
      "number_of_shards": 1,
      "number_of_replicas": 1
    }
  }
}
```

## Компонентный шаблон с маппингом для логов
```json
PUT _component_template/logs_mapping
{
  "template": {
    "mappings": {
      "properties": {
        "@timestamp": { "type": "date" },
        "level": { "type": "keyword" },
        "message": { "type": "text" }
      }
    }
  }
}
```

## Индексный шаблон, использующий компоненты
```json
PUT _index_template/logs_template
{
  "index_patterns": ["logs-*"],
  "composed_of": ["common_settings", "logs_mapping"],
  "template": {
    "aliases": {
      "logs": {}
    }
  }
}
```

## Работа с Data Streams
```json
PUT _index_template/metrics_stream_template
{
  "index_patterns": ["metrics-*"],
  "data_stream": {},
  "template": {
    "mappings": {
      "properties": {
        "@timestamp": { "type": "date" },
        "value": { "type": "float" }
      }
    }
  }
}
```

## Создание data stream
```json
PUT _data_stream/metrics-prod
```

## Запись документа в data stream
```json
POST metrics-prod/_doc
{
  "@timestamp": "2025-01-15T10:30:00Z",
  "value": 42.5
}
```

## Ручной ролловер (принудительное создание нового backing-индекса)
```json
POST metrics-prod/_rollover
```




