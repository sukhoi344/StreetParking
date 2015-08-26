package chau.streetparking.backend;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.*;
import android.os.Process;

import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import chau.streetparking.FileManager;
import chau.streetparking.datamodels.parse.ParkingLot;
import chau.streetparking.datamodels.parse.User;
import chau.streetparking.util.FileUtil;
import chau.streetparking.util.ImageUtil;
import chau.streetparking.util.Logger;

/**
 * Created by Chau Thai on 8/26/15.
 */
public class GarageCreator {
    private static final int MAX_IMAGE_DIMENSION = 1024;

    private final Context context;
    private final User owner;
    private final String name;
    private final int capacity;
    private final LatLng latLng;
    private final String address;
    private final double price;
    private final String priceType;
    private final String info;
    private final List<Uri> photoUriList;
    private final SaveCallback saveCallback;

    private List<ParseFile> parseFiles = new ArrayList<>();
    private boolean isWorking = false;

    private GarageCreator(Builder builder) {
        context     = builder.context;
        owner       = builder.owner;
        name        = builder.name;
        capacity    = builder.capacity;
        latLng      = builder.latLng;
        address     = builder.address;
        price       = builder.price;
        priceType   = builder.priceType;
        info        = builder.info;
        photoUriList = builder.photoUriList;
        saveCallback = builder.saveCallback;
    }

    public void createGarage() {
        if (!isWorking) {
            isWorking = true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                    try {
                        savePhoto();
                        ParkingLot parkingLot = new ParkingLot();
                        parkingLot.setOwner(owner);
                        parkingLot.setName(name);
                        parkingLot.setCapacity(capacity);
                        parkingLot.setInUse(0);
                        parkingLot.setLocation(new ParseGeoPoint(latLng.latitude, latLng.longitude));
                        parkingLot.setAddress(address);
                        parkingLot.setPrice(price);
                        parkingLot.setPriceType(priceType);
                        parkingLot.setInfo(info);

                        if (parseFiles != null && !parseFiles.isEmpty())
                            parkingLot.setPhotos(parseFiles);

                        parkingLot.save();
                        saveCallback.done(null);

                    } catch (ParseException e) {
                        Logger.printStackTrace(e);
                        if (saveCallback != null)
                            saveCallback.done(e);
                    }
                }
            }).start();
        }
    }

    private void savePhoto() {
        if (photoUriList != null && !photoUriList.isEmpty()) {
            for (Uri uri : photoUriList) {
                try {
                    Bitmap bitmap = ImageUtil.getBitmapFromUriChooser(context, uri, MAX_IMAGE_DIMENSION);
                    byte[] data = bitmapToByte(bitmap);

                    ParseFile parseFile = new ParseFile(FileManager.getPhotoFileName(owner.getObjectId()), data);
                    parseFiles.add(parseFile);

                } catch (Exception e) {
                    Logger.printStackTrace(e);
                    return;
                }
            }

            for (ParseFile file : parseFiles) {
                try {
                    file.save();
                } catch (Exception e) {
                    Logger.printStackTrace(e);
                    return;
                }
            }
        }
    }

    private byte[] bitmapToByte(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            return byteArray;
        }

        return null;
    }

    public static class Builder {
        private Context context;
        private User owner;
        private String name;
        private int capacity;
        private LatLng latLng;
        private String address;
        private double price;
        private String priceType;
        private String info = "";
        private List<Uri> photoUriList = new ArrayList<>();
        private SaveCallback saveCallback;

        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setOwner(User owner) {
            this.owner = owner;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setCapacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder setLatLng(LatLng latLng) {
            this.latLng = latLng;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setPrice(double pride) {
            this.price = pride;
            return this;
        }

        public Builder setPriceType(String priceType) {
            this.priceType = priceType;
            return this;
        }

        public Builder setInfo(String info) {
            this.info = info;
            return this;
        }

        public Builder setPhotoUriList(List<Uri> photoUriList) {
            this.photoUriList = photoUriList;
            return this;
        }

        public Builder setSaveCallBack(SaveCallback saveCallBack) {
            this.saveCallback = saveCallBack;
            return this;
        }

        public GarageCreator build() {
            return new GarageCreator(this);
        }
    }

}
