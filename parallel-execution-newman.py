import subprocess
import json
import random

with open('CVC_Apis.postman_collection.json', 'r') as f:
    collection = json.load(f)

random.shuffle(collection['item'])

chunks = [collection['item'][i:i+10] for i in range(0, len(collection['item']), 20)]

for i, chunk in enumerate(chunks):
    command = f"newman run https://api.postman.com/collections/25156296-4a3d8161-02a7-4fd4-946e-03b0e9a2be5f?access_key=PMAT-01GTXS776P4X8A9WFZHV4DS293 --env-var concurrency={len(chunk)} --reporters cli,junit --reporter-junit-export report.xml"
    proc = subprocess.Popen(command, shell=True)
    proc.wait()

print(f"Started {len(chunks)} parallel processes for {len(collection['item'])} requests.")
