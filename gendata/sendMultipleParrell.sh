myfile="kill.file"
while [ ! -e $myfile ]; do 

temper=$((RANDOM % 8))
echo "Sending Random Request. Waiting $temper seconds until next request "
for server in [0..temper];
do
./sendMyRequestQueue.sh >/dev/null 2>&1 &
#sleep  $((RANDOM % 20))
#sleep temper
done
sleep  $((RANDOM % 5))
done
