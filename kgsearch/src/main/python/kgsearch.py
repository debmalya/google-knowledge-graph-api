"""Example of Python client calling Knowledge Graph Search API."""
import json
import urllib

api_key = open('./api_key').read()
query = raw_input( 'What to search :')
service_url = 'https://kgsearch.googleapis.com/v1/entities:search'
params = {
    'query': query,
    'limit': 100,
    'indent': True,
    'key': api_key,
}
url = service_url + '?' + urllib.urlencode(params)
response = json.loads(urllib.urlopen(url).read())
for element in response['itemListElement']:
  if 'name' in element['result'] and 'description' in element['result']:
#      print element['result']['name'] + ' (' + str(element['resultScore']) + ')'
      print element['result']['name'] + ' ' + element['result']['description']
