import requests
import os
import json

response = requests.post('https://xray.cloud.getxray.app/api/v2/authenticate', data={'client_id': 'CC989C0AA51D431AA2FC9F7B43DC9847', 'client_secret': '497adab68f8559e6199904f3f4d8744a205ce8eaad0f3495fd95722172b548b4'})
parsed_auth = response.json()
token = 'Bearer {}'.format(parsed_auth)
print(token)



