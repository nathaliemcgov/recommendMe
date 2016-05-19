from bs4 import BeautifulSoup
import urllib
import csv

artistList = []

url = 'http://www.imdb.com/search/title?groups=top_1000&sort=user_rating&view=simple'
page = urllib.urlopen(url)
soup = BeautifulSoup(page.read())

artistName = soup.find_all('a', href=True)

for artist in artistName:
    artistList.append([unicode(artist.text).encode("utf-8")])

musicFile = open('imdb1000movies.csv', 'w')
writer = csv.writer(musicFile)

print len(artistList)
writer.writerows(artistList)