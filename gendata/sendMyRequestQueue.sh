#!/bin/sh
value=$RANDOM

#value=15931
tmpdir="tmpdir"
#rm -rf $tmpdir
Suites="BUTTER JAM LAPTOP BUTTER CHEESE CHEESE"

customers="Fred Fred Fred Fred Ivan Tom Joe Eric Ivan Gary Eric"

#faults="Business_fault Technical_fault"

#Suites="provisionContract renewContract"
suite=($Suites)
num_suites=${#suite[*]}
product_type=${suite[$((RANDOM%num_suites))]}



customer=($customers)
num_customer=${#customer[*]}
customer_type=${customer[$((RANDOM%num_customer))]}


echo $operation_type

mkdir $tmpdir
filename="SampleRequest"
payment="payment"

url="http://localhost:8080/demo-orders/OrderService"

echo "********* [BEGIN] Simulating for generated id $value ************"

namer="ivanservice"

echo $timer

timer=`date +%s`
file="$tmpdir/output.$filename.$value.xml"
tmpfile="$tmpdir/output.$filename.$value.json.xml"
cp ${filename}.json ${file}

file_pay="$tmpdir/output.$payment.$value.xml"
tmpfile_pay="$tmpdir/output.$payment.$value.json.xml"
cp ${payment}.json ${file_pay}

 # file="$tmpdir/output.$filename.$value.json"
   placeHolder="TIMESTAMP_REPLACE_${i}_"
  sed  "s/ID_REPLACE/${value}/g"  "${file}" | \
  sed -e "s/PRODUCT_REPLACE/${product_type}/g"|  sed -e "s/CUSTOMER_REPLACE/${customer_type}/g"  > "${tmpfile}"
   sed  "s/ID_REPLACE/${value}/g"  "${file_pay}" | \
    sed -e "s/PRODUCT_REPLACE/${product_type}/g"|  sed -e "s/CUSTOMER_REPLACE/${customer_type}/g"  > "${tmpfile_pay}"

myDelay=$((RANDOM % 100))
echo "******* Creating artificial delay of [${myDelay}] ms ******* "

let timer=$timer+$myDelay
#fi
echo "Performing phase [$i] and operation ${product_type} for conversationid=$value"
cp ${tmpfile} ${file}

#sleep $((RANDOM % 10))


echo "********* [END] Simulating for generated id $value ************"

echo "********* [Sending] ${url} for generated file id $value,${file} ************"
curl -X POST  -H "Content-Type: text/xml; charset=utf-8"   -d "@${file}" "${url}"  

sleep 1

curl -X POST  -H "Content-Type: text/xml; charset=utf-8"   -d "@${tmpfile_pay}" "${url}"  

#rm ${tmpfile}
#rm ${file}
echo ""


