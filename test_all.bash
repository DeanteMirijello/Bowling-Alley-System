#!/usr/bin/env bash

: ${HOST=localhost}
: ${PORT=8080}
BASE_URL="http://${HOST}:${PORT}/api"

ballId=""
shoeId=""
laneId=""
transactionId=""

function assertCurl() {
  local expectedHttpCode=$1
  local curlCmd="$2 -w \"%{http_code}\""
  local result=$(eval $curlCmd)
  local httpCode="${result:(-3)}"
  RESPONSE='' && (( ${#result} > 3 )) && RESPONSE="${result%???}"

  if [ "$httpCode" = "$expectedHttpCode" ]; then
    echo "✅ Test OK (HTTP $httpCode)"
  else
    echo "❌ Test FAILED: expected HTTP $expectedHttpCode but got $httpCode"
    echo "- Command: $curlCmd"
    echo "- Response: $RESPONSE"
    exit 1
  fi
}

function testUrl() {
  url=$@
  if curl $url -ks -f -o /dev/null; then
    echo "✓ Ready"
    return 0
  else
    echo -n "."
    return 1
  fi
}

function waitForService() {
  url=$@
  echo -n "Waiting for: $url"
  for ((i=0; i<50; i++)); do
    if testUrl $url; then return 0; fi
    sleep 4
  done
  echo "❌ Timeout waiting for $url"
  exit 1
}

if [[ $@ == *"start"* ]]; then
  echo "🔁 Restarting environment..."
  docker compose down
  docker compose up -d
fi

waitForService curl -X GET "$BASE_URL/balls"

# === BOWLING BALL SERVICE ===
echo -e "\n🎳 Creating Bowling Ball..."
ballBody='{
  "size": "TEN",
  "gripType": "Standard",
  "color": "Red",
  "status": "AVAILABLE"
}'
ballResponse=$(curl -s -X POST "$BASE_URL/balls" -H "Content-Type: application/json" -d "$ballBody")
echo "$ballResponse" | jq .

ballId=$(echo "$ballResponse" | jq -r '.id')
if [[ "$ballId" == "null" || -z "$ballId" ]]; then
  echo "❌ Failed to create Bowling Ball."
  exit 1
fi

# -- GET BY ID --
echo -e "\n🔍 Getting Bowling Ball by ID..."
assertCurl 200 "curl $BASE_URL/balls/$ballId -s"

# -- GET ALL Bowling Balls --
echo -e "\n📚 Getting All Bowling Balls..."
assertCurl 200 "curl $BASE_URL/balls -s"

# -- PUT --
echo -e "\n✏️ Updating Bowling Ball..."
ballPutBody='{
  "size": "TEN",
  "gripType": "Fingertip",
  "color": "Blue",
  "status": "IN_USE"
}'
assertCurl 200 "curl -X PUT $BASE_URL/balls/$ballId -H 'Content-Type: application/json' -d '$ballPutBody' -s"

# === SHOE SERVICE ===
echo -e "\n👟 Creating Shoe..."
shoeBody='{
  "size": "SIZE_10",
  "purchaseDate": "2024-05-01",
  "status": "AVAILABLE"
}'
shoeResponse=$(curl -s -X POST "$BASE_URL/shoes" -H "Content-Type: application/json" -d "$shoeBody")
echo "$shoeResponse" | jq .
shoeId=$(echo "$shoeResponse" | jq -r '.id')
if [[ "$shoeId" == "null" || -z "$shoeId" ]]; then
  echo "❌ Failed to create Shoe."
  exit 1
fi

# -- GET BY ID --
echo -e "\n🔍 Getting Shoe by ID..."
assertCurl 200 "curl $BASE_URL/shoes/$shoeId -s"

# -- GET ALL Shoes --
echo -e "\n📚 Getting All Shoes..."
assertCurl 200 "curl $BASE_URL/shoes -s"


# -- PUT --
echo -e "\n✏️ Updating Shoe..."
shoePutBody='{
  "size": "SIZE_11",
  "purchaseDate": "2024-05-05",
  "status": "IN_USE"
}'
assertCurl 200 "curl -X PUT $BASE_URL/shoes/$shoeId -H 'Content-Type: application/json' -d '$shoePutBody' -s"

# === LANE SERVICE ===
echo -e "\n🛣️ Creating Lane..."
laneBody='{
  "laneNumber": 42,
  "zone": "ZONE_1",
  "status": "MAINTENANCE"
}'
laneResponse=$(curl -s -X POST "$BASE_URL/lanes" -H "Content-Type: application/json" -d "$laneBody")
echo "$laneResponse" | jq .
laneId=$(echo "$laneResponse" | jq -r '.id')
if [[ "$laneId" == "null" || -z "$laneId" ]]; then
  echo "❌ Failed to create Lane."
  exit 1
fi

# -- GET BY ID --
echo -e "\n🔍 Getting Lane by ID..."
assertCurl 200 "curl $BASE_URL/lanes/$laneId -s"

# -- GET ALL Lanes --
echo -e "\n📚 Getting All Lanes..."
assertCurl 200 "curl $BASE_URL/lanes -s"

# -- PUT --
echo -e "\n✏️ Updating Lane..."
lanePutBody='{
  "laneNumber": 42,
  "zone": "ZONE_2",
  "status": "AVAILABLE"
}'
assertCurl 200 "curl -X PUT $BASE_URL/lanes/$laneId -H 'Content-Type: application/json' -d '$lanePutBody' -s"

# --- POST Transaction ---
echo -e "\n📄 Creating Transaction..."

# Build the JSON body without dateCompleted (not accepted in request DTO)
txBody=$(cat <<EOF
{
  "customerName": "John Smith",
  "status": "OPEN",
  "bowlingBallId": "$ballId",
  "shoeId": "$shoeId",
  "laneId": "$laneId"
}
EOF
)

# Validate IDs before sending
if [[ -z "$ballId" || -z "$shoeId" || -z "$laneId" ]]; then
  echo "❌ One or more required IDs are missing."
  exit 1
fi

# Execute POST
txResponse=$(echo "$txBody" | curl -s -X POST "$BASE_URL/transactions" -H "Content-Type: application/json" -d @-)
echo "$txResponse" | jq .

# Extract transactionId
transactionId=$(echo "$txResponse" | jq -r '.transactionId')

if [[ "$transactionId" == "null" || -z "$transactionId" ]]; then
  echo "❌ Failed to create Transaction. Check validation rules."
  exit 1
fi

assertCurl 200 "curl $BASE_URL/transactions/$transactionId -s"

# -- GET BY ID --
echo -e "\n🔍 Getting Transaction by ID..."
assertCurl 200 "curl $BASE_URL/transactions/$transactionId -s"

# --- GET ALL Transactions ---
echo -e "\n📚 Fetching All Transactions..."
assertCurl 200 "curl $BASE_URL/transactions -s"

# --- PUT Transaction ---
echo -e "\n✏️ Updating Transaction..."
txPutBody=$(cat <<EOF
{
  "customerName": "John Smith Jr.",
  "status": "COMPLETED",
  "bowlingBallId": "$ballId",
  "shoeId": "$shoeId",
  "laneId": "$laneId"
}
EOF
)
assertCurl 200 "curl -X PUT $BASE_URL/transactions/$transactionId -H 'Content-Type: application/json' -d '$txPutBody' -s"

# --- Negative Test: GET Invalid Transaction ID ---
echo -e "\n🧪 Negative Test: Invalid Transaction ID..."
assertCurl 404 "curl $BASE_URL/transactions/00000000-0000-0000-0000-000000000000 -s"

# --- DELETE Transaction ---
echo -e "\n❌ Deleting Transaction..."
assertCurl 204 "curl -X DELETE $BASE_URL/transactions/$transactionId -s"

# -- DELETE --
echo -e "\n❌ Deleting Bowling Ball..."
assertCurl 204 "curl -X DELETE $BASE_URL/balls/$ballId -s"

# -- DELETE --
echo -e "\n❌ Deleting Lane..."
assertCurl 204 "curl -X DELETE $BASE_URL/lanes/$laneId -s"

# -- DELETE --
echo -e "\n❌ Deleting Shoe..."
assertCurl 204 "curl -X DELETE $BASE_URL/shoes/$shoeId -s"

if [[ $@ == *"stop"* ]]; then
  echo "🛑 Stopping Docker..."
  docker compose down
fi

echo -e "\n✅ All tests passed successfully!"
