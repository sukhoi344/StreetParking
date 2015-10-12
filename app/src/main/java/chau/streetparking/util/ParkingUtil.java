package chau.streetparking.util;

import java.util.Date;

import chau.streetparking.datamodels.parse.ParkingLot;

/**
 * Created by Chau Thai on 10/12/15.
 */
public class ParkingUtil {
    public static int getPrice(ParkingLot parkingLot, Date startDate, Date endDate) {
        final long startTime = startDate.getTime();
        final long endTime = endDate.getTime();

        if (endTime <= startTime)
            return 0;

        long totalMinutes = (endTime - startTime) / (1000 * 60);

        switch (parkingLot.getPriceType()) {
            case ParkingLot.PriceType.HOURLY:
                return (int) (parkingLot.getPrice() * (totalMinutes / 60.0));
            case ParkingLot.PriceType.DAILY:
                return (int) (parkingLot.getPrice() / 24.0 * (totalMinutes / 60.0));
            case ParkingLot.PriceType.MONTHLY:
                return (int) (parkingLot.getPrice() / 24.0 / 30 * (totalMinutes / 60.0));
            default:
                return (int) (parkingLot.getPrice() * (totalMinutes / 60.0));
        }

    }
}
