import subprocess
import json
import random

with open('CVC_Apis.postman_collection.json', 'r') as f:
    collection = json.load(f)

random.shuffle(collection['item'])

chunks = [collection['item'][i:i+10] for i in range(0, len(collection['item']), 20)]

for i, chunk in enumerate(chunks):
    command = f"newman run https://api.postman.com/collections/18481634-926bcc97-e8db-4354-ac57-e293cd106587?access_key=PMAT-01GV2Y8JN6HSKEW0PRMRKK8GVA --env-var concurrency={len(chunk)} --reporters cli,junit --reporter-junit-export report.xml"
    proc = subprocess.Popen(command, shell=True)
    proc.wait()

print(f"Started {len(chunks)} parallel processes for {len(collection['item'])} requests.")
