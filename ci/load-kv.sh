
echo "⏳ Ожидаем запуска Consul..."
until curl -s http://localhost:8500/v1/status/leader | grep -q '"'; do
  sleep 1
done
echo "✅ Consul доступен. Загружаем KV-данные..."

for key in $(jq -r 'keys[]' /kv-data.json); do
  value=$(jq -r --arg key "$key" '.[$key]' /kv-data.json)
  echo "📥 Загружаем $key = $value"
  curl -s -X PUT --data "$value" http://localhost:8500/v1/kv/$key > /dev/null
done

echo "✅ Загрузка завершена"