from bs4 import BeautifulSoup
import urllib
import csv

artistList = []

url = 'http://www.acclaimedmusic.net/Current/1890-19art.htm'
page = urllib.urlopen(url)
soup = BeautifulSoup(page.read())

artistName = soup.find_all('a', href=True)

for artist in artistName:
    artistList.append([unicode(artist.text).encode("utf-8")])

musicFile = open('acclaimedmusic1000.csv', 'w')
writer = csv.writer(musicFile)

print len(artistList)
writer.writerows(artistList)