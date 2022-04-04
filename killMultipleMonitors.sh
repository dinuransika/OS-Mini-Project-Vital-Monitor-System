for i in $(seq 5)
do
    j=$((i + 5000));
    echo "Killing Vital Monitor: CICU_$i at port: $j"
    kill -9 $(lsof -t -i:$j)
done