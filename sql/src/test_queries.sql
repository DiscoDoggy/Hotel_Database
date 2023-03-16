
--prints hotels in range of 30
SELECT hotelName
FROM Hotel
WHERE calculate_distance(1.1,50.1,latitude,longitude) < 30;

--print rooms at a hotel with the hotelID and date as input
SELECT roomNumber, price, 
FROM Rooms R, RoomBookings RB
WHERE R.hotelID = R.hotelID AND

EXCEPT

SELECT roomNumber
FROM RoomBookings
WHERE R.hotelID = R.hotelID AND bookingDate NOT userDate; --second hotel id is the user inputted hotel idi







