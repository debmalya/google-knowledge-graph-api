"""Example of Python client calling Knowledge Graph Search API."""
import json
import urllib

api_key = open('./api_key').read()
query = raw_input( 'What to search :')
service_url = 'https://kgsearch.googleapis.com/v1/entities:search'
params = {
    'query': query,
    'limit': 10,
    'indent': False,
    'key': api_key,
}
url = service_url + '?' + urllib.urlencode(params)
response = json.loads(urllib.urlopen(url).read())
found = False
result = ""
for element in response['itemListElement']:
  found = True
  if 'name' in element['result'] and 'detailedDescription' in element['result'] and 'articleBody' in element['result']['detailedDescription']:
      result += element['result']['detailedDescription']['articleBody'] + ' '
if not found:
    result =  query + ' not found'
print result
