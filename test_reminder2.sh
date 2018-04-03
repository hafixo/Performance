#!/bin/bash
# written by Kirill Grushin (kirill.grushin@dev.zodiac.tv)

curlwrap="curl"
#curlwrap="curl -v"
#curlwrap="curl -v --connect-timeout 5"

#RED='\033[0;31m'
#GREEN='\033[0;32m'
#GRAY='\033[0;37m'

macaddress="$1"
action="$2"
param="$3"
count_reminders="$4"

ams_ip="172.30.81.4"
#ams_ip="172.30.112.19"
#ams_ip="172.30.82.132"
ams_port="8080"

count_iterations=1
reminderProgramId=EPO
reminderScheduleId=1
reminderId=1
reminderChannelNumber=2
reminderOffset=0

RACK_DATE=( `date +%Y-%m-%d -d "tomorrow"` )
#RACK_DATE=( `date +%Y-%m-%d -d "tomorrow +1day"` `date +%Y-%m-%d -d "tomorrow +2day"` `date +%Y-%m-%d -d "tomorrow +3day"` `date +%Y-%m-%d -d "tomorrow +4day"` `date +%Y-%m-%d -d "tomorrow +5day"` `date +%Y-%m-%d -d "tomorrow +6day"` `date +%Y-%m-%d -d "tomorrow +7day"` `date +%Y-%m-%d -d "tomorrow +8day"` `date +%Y-%m-%d -d "tomorrow +9day"` `date +%Y-%m-%d -d "tomorrow +10day"` )


RACK_CHANNELS=( 2 )
#RACK_CHANNELS=( 2 3 4 5 6 7 8 9 12 13 14 16 18 19 22 23 25 28 30 31 32 33 37 38 41 44 46 49 50 51 )

charterapi_a="http://spec.partnerapi.engprod-charter.net/api/pub"
charterapi_b="http://specb.partnerapi.engprod-charter.net/api/pub"
charterapi_c="http://specc.partnerapi.engprod-charter.net/api/pub"
charterapi_d="http://specd.partnerapi.engprod-charter.net/api/pub"
#charterapi_d="http://specd.partnerapi.engprod-charter.net/api/pub"
postfix_settings="networksettingsmiddle/ns/settings"
charterapi="$charterapi_b"

function generate_params(){
if [ "$action" == "Add" ]; then url="ams/Reminders?req=add"; source test_reminder_add.sh
if [ "$param" == "48" ]; then reminder="reminder_add48"
elif [ "$param" == "288" ]; then reminder="reminder_add288"
elif [ "$param" == "720" ]; then reminder="reminder_add720"
else reminder="reminder_add1"
fi

elif [ "$action" == "Modify" ]; then url="ams/Reminders?req=modify"; source test_reminder_modify.sh
if [ "$param" == "48" ]; then reminder="reminder_modify48"
elif [ "$param" == "288" ]; then reminder="reminder_modify288"
elif [ "$param" == "720" ]; then reminder="reminder_modify720"
else reminder="reminder_modify1"
fi

elif [ "$action" == "Delete" ]; then url="ams/Reminders?req=delete"; source test_reminder_delete.sh
if [ "$param" == "48" ]; then reminder="reminder_delete48"
elif [ "$param" == "288" ]; then reminder="reminder_delete288"
elif [ "$param" == "720" ]; then reminder="reminder_delete720"
else reminder="reminder_delete1"
fi
fi
}

#url="ams/Reminders?req=ChangeReminders"
logfile="test_reminder.log"
logwrap="tee -a $logfile"
startmessage="[DBG] `date "+%a %b %d %T %N %Z %Y"`: NEW START: ams_ip=$ams_ip, count_reminders=$param, count_iterations=$count_iterations, RACK_DATE=( ${RACK_DATE[@]} ), RACK_CHANNELS=( ${RACK_CHANNELS[@]} )"

synopsys="\nNAME
\ttest_reminder.sh - script for Add / Edit / Delete / Purge reminders on MACADDRESS and also check registration / registration on AMS.
SYNOPSYS
\ttest_reminder.sh [MACADDRESS] [OPERATION] [COUNT]
\nDESCRIPTION
\tMACADDRESS is a box macaddress, e.g. A0722CB1AF24
\tOPERATION is a action for curl/json e.g. Add, Edit (it's really will be Delete+Add), Delete, Purge, Change
\tCOUNT is a count of reminders, can be 48, 288, 720 in one curl request
OPTIONS
\ttest_Reminder.sh                   - print this help
\ttest_reminder.sh MACADDRESS        - send curl for checking registration
\ttest_reminder.sh MACADDRESS Check  - send curl for checking registration
\ttest_reminder.sh MACADDRESS Change - send curl for changing registration to new AMS
\ttest_reminder.sh MACADDRESS Purge  - clear all reminders
\ttest_reminder.sh MACADDRESS Add    - add reminders (cyclically)
\ttest_reminder.sh MACADDRESS Edit   - delete reminders with current offset + add reminders with new_offset (cyclically)
\ttest_reminder.sh MACADDRESS Delete - delete reminders (cyclically)
\ttest_reminder.sh MACADDRESS All    - add + edit + delete reminders (cyclically)
CURRENT SETTINGS
\tAMS: $ams_ip
\tcharterapi: $charterapi
\tcount of reminders in one request: $count_reminders
\tcount iterations: $count_iterations
STATUSCODE
\tcode of the reminder processing result, one of the following:
\t0 - requested operation with the reminder was accomplished successfully. Always returned for \"Reminders Purge\" request (Request ID=3)
\t1 - TBD
\t2 - reminder is set for time in the past. Applies to \"Reminders Add\" request (Request ID=0)
\t3 - reminder is set for unknown channel. \"Reminders Add\" request (Request ID=0)
\t4 - reminder is unknown. Applies to \"Reminders Delete\" request (Request ID=1) and \"Reminders Modify\" request (Request ID=2)
\t5 - reminder with provided pair of identifiers (reminderScheduleId and reminderId) is already set \"Reminders Add\" request (Request ID=0)
\thttps://svn.developonbox.ru/Charter_Docs/Projects/Cloud_Based_Guide/Requirements/APIs/Reminders%20Propagation%20API-v6.docx"

statuscodes="code of the reminder processing result, one of the following:
0 - requested action with the reminder was accomplished successfully
2 - reminder is set for time in the past
3 - reminder is set for unknown channel
4 - reminder is unknown, applies to reminder deletion attempts"
########################### ########################### ########################### ########################### ###########################
########################### ########################### ########################### ########################### ###########################
########################### ########################### ########################### ########################### ###########################

#if [ ! -x "`which time`" ]; then echo -e "No time detected! Please check.$synopsys"; exit
#else time="/usr/bin/time -f 'real %Es' -o $logfile -a"
#fi

if [ ! -x "`which curl`" ]; then echo -e "No curl detected! please check.$synopsys"; exit; fi

########################### main() ###########################
if [ -z "$macaddress" ]; then
echo -e "No macaddress specified!$synopsys"|$logwrap; exit;
elif [ `expr length "$macaddress"` -ne "12" ]; then echo -e "Incorrect macaddress specified!$synopsys"|$logwrap; exit
fi

if [ -z "$count_reminders" ]; then count_reminders=1; fi

if [ ! -z "$action" ]; then


if [ "$action" == "Check" ]; then
echo "Checking registration:"
$curlwrap $charterapi_a/$postfix_settings/amsIp/$macaddress; echo
$curlwrap $charterapi_b/$postfix_settings/amsIp/$macaddress; echo
$curlwrap $charterapi_c/$postfix_settings/amsIp/$macaddress; echo
$curlwrap $charterapi_d/$postfix_settings/amsIp/$macaddress; echo


elif [ "$action" == "Change" ]; then
if [ ! -z "$param" ]; then ams_ip=$param; fi

echo "Changing registration to AMS $ams_ip:"
echo "Used charterapi: $charterapi"
echo "Used ams:port: $ams_ip:$ams_port"
$curlwrap -H 'Content-Type:application/json' -d '{"settings":{"groups":[{"options":[],"id":"STB'$macaddress'","type":"device-stb","amsid":"'$ams_ip'"}]}}' "$charterapi_a/$postfix_settings?requestor=AMS"; echo
$curlwrap -H 'Content-Type:application/json' -d '{"settings":{"groups":[{"options":[],"id":"STB'$macaddress'","type":"device-stb","amsid":"'$ams_ip'"}]}}' "$charterapi_b/$postfix_settings?requestor=AMS"; echo
$curlwrap -H 'Content-Type:application/json' -d '{"settings":{"groups":[{"options":[],"id":"STB'$macaddress'","type":"device-stb","amsid":"'$ams_ip'"}]}}' "$charterapi_c/$postfix_settings?requestor=AMS"; echo
$curlwrap -H 'Content-Type:application/json' -d '{"settings":{"groups":[{"options":[],"id":"STB'$macaddress'","type":"device-stb","amsid":"'$ams_ip'"}]}}' "$charterapi_d/$postfix_settings?requestor=AMS"; echo


elif [ "$action" == "Purge" ]; then
url="ams/Reminders?req=purge"
echo "[DBG] `date "+%a %b %d %T %N %Z %Y"`: NEW START: ams_ip=$ams_ip, Purge ---> (_|_)"|$logwrap
/usr/bin/time -f 'real %Es' -o $logfile -a $curlwrap 'http://'$ams_ip':'$ams_port'/'$url'' -H 'Content-type: application/json' --data '{ "deviceId": '$macaddress', "reminders": []}'; echo
#/usr/bin/time -f 'real %Es' curl -s 'http://'$ams_ip':'$ams_port'/'$url'' -H 'Content-type: text/plain' --data '{ "deviceId": '$macaddress', "reminders": [{"operation": "Purge"}]}' 2>&1|tee -a $logfile; echo


#elif [ "$action" == "All" ]; then
#check_param
#echo $startmessage|$logwrap
#for (( i=1; i<=$count_iterations; i++)); do for data in ${RACK_DATE[@]}; do for channel in ${RACK_CHANNELS[@]}; do
#$reminder $macaddress $reminderProgramStart $reminderChannelNumber $; sleep 1
##$reminder Edit $Offset $Offset_new
#$reminder Delete $Offset; sleep 1
#one; done; done
#echo "$statuscodes"


elif [ "$action" == "Add" ]; then
generate_params
echo $startmessage|$logwrap
for (( i=1; i<=$count_iterations; i++)); do for reminderProgramStart in ${RACK_DATE[@]}; do for reminderChannelNumber in ${RACK_CHANNELS[@]}; do
${reminder} ${macaddress} $reminderProgramStart $reminderChannelNumber $reminderProgramId $reminderScheduleId $reminderId $reminderOffset;  sleep 1; done; done; done


elif [ "$action" == "Modify" ]; then
generate_params
echo $startmessage|$logwrap
for (( i=1; i<=$count_iterations; i++)); do for reminderProgramStart in ${RACK_DATE[@]}; do for reminderChannelNumber in ${RACK_CHANNELS[@]}; do
${reminder} ${macaddress} $reminderProgramStart $reminderChannelNumber $reminderProgramId $reminderScheduleId $reminderId $reminderOffset;  sleep 1; done; done; done


elif [ "$action" == "Delete" ]; then
generate_params
echo $startmessage|$logwrap
for (( i=1; i<=$count_iterations; i++)); do for reminderProgramStart in ${RACK_DATE[@]}; do for reminderChannelNumber in ${RACK_CHANNELS[@]}; do
${reminder} ${macaddress} $reminderProgramStart $reminderChannelNumber $reminderProgramId $reminderScheduleId $reminderId $reminderOffset;  sleep 1; done; done; done

else
echo -e "Incorrect action specified!$synopsys"|$logwrap; exit;


fi
else
echo "Checking registration:"
$curlwrap $charterapi_a/$postfix_settings/amsIp/$macaddress; echo
$curlwrap $charterapi_b/$postfix_settings/amsIp/$macaddress; echo
$curlwrap $charterapi_c/$postfix_settings/amsIp/$macaddress; echo
$curlwrap $charterapi_d/$postfix_settings/amsIp/$macaddress; echo

fi
########################### main() end ###########################