/*
Updating the booking reference to be a code of 5 characters excluding 0,1,O,I where the booking reference is null.
Inside the concat function, we create the code letter by letter. This is done by getting a random character from the substring specified
with min index being 1 and max being 31. The second parameter (1) in the SUBSTRING function specifies the number of character that we want.
Then we concat all of the separate characters into one code and update booking_reference.
*/
UPDATE booking
SET booking_reference = (Select CONCAT(
	SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(RAND()*31+1), 1),
	SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(RAND()*31+1), 1),
	SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(RAND()*31+1), 1),
	SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(RAND()*31+1), 1),
	SUBSTRING('ABCDEFGHJKLMNPQRSTUVWXYZ23456789', FLOOR(RAND()*31+1), 1)
))
WHERE booking_reference IS NULL;
