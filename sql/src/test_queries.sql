
SELECT hotelName
FROM Hotel
WHERE calculate_distance(1,50,latitude,longitude) < 30;








