package com.mixer.raw;

import java.io.*;

/**
 * Handling data base file
 *
 * @version   1.0 18 Oct 2019
 * @author    Oleg Khlebnikov
 */

public class FileHandler {
    private RandomAccessFile dbFile;

    /**
     * Constructor to open a db file
     * @param dbFileName name of our db file
     * @throws FileNotFoundException
     */
    public FileHandler(final String dbFileName) throws FileNotFoundException {
        this.dbFile = new RandomAccessFile(dbFileName, "rw"); //'rw' stays for 'read' and 'write'
    }

    /**
     * Save data into the file
     * @param name
     * @param age
     * @param address
     * @param carPlateNumber
     * @param description
     * @return bool value whether information was successfully added or not
     */
    public boolean add(String name,
                       int age,
                       String address,
                       String carPlateNumber,
                       String description) throws IOException {
        /** seek for the end of the file */
        long currentPositionToInsert = this.dbFile.length();
        this.dbFile.seek(currentPositionToInsert);

        // isDeleted information, 1 byte
        // recordLength : int
        // name length : int
        // name
        // address length : int
        // address
        // car plate number length : int
        // car plate number
        // description length : int
        // description

        /** calculate length of the record */
        int length = 4 + // name length (int, 4 bytes)
                name.length() +
                4 + // age
                4 + // address length
                address.length() +
                4 + // car plate number length
                carPlateNumber.length() +
                4 + // description length
                description.length();

        /** set whether it's deleted, false because it's new record */
        this.dbFile.writeBoolean(false);
        /** set record length */
        this.dbFile.writeInt(length);

        /** store the name */
        this.dbFile.writeInt(name.length());
        this.dbFile.write(name.getBytes());

        /** store age */
        this.dbFile.writeInt(age);

        /** store the address */
        this.dbFile.writeInt(address.length());
        this.dbFile.write(address.getBytes());

        /** store the car plate number */
        this.dbFile.writeInt(carPlateNumber.length());
        this.dbFile.write(carPlateNumber.getBytes());

        /** store the description */
        this.dbFile.writeInt(description.length());
        this.dbFile.write(description.getBytes());

        Index.getInstance().add(currentPositionToInsert);

        return true;
    }

    /**
     * Read the information from the file, and put it in the Person
     * @param rowNumber index component
     * @return
     * @throws IOException
     */
    public Person readRow(int rowNumber) throws IOException {
        long bytePosition = Index.getInstance().getBytePosition(rowNumber);
        if(bytePosition == -1) {
            return null;
        }

        byte[] row = this.readRawRecord(bytePosition);
        Person person = new Person();
        DataInputStream stream = new DataInputStream(new ByteArrayInputStream(row)); //to read from the byte array

        //reading the name
        int nameLength = stream.readInt();
        byte[] b = new byte[nameLength];
        stream.read(b);
        person.name = new String(b);

        //reading the age
        person.age = stream.readInt();

        //reading the address
        b = new byte[stream.readInt()];
        stream.read(b);
        person.address = new String(b);

        //reading the car plate num
        b = new byte[stream.readInt()];
        stream.read(b);
        person.carPlateNumber = new String(b);

        // reading the description
        b = new byte[stream.readInt()];
        stream.read(b);
        person.description = new String(b);

        return person;
    }

    /**
     * Reads raw information (bytes) from the file
     * @param bytPositionOfRow
     * @return
     * @throws IOException
     */
    private byte[] readRawRecord(long bytPositionOfRow) throws IOException {
        this.dbFile.seek(bytPositionOfRow);
        if (this.dbFile.readBoolean()) {
            return new byte[0];
        }
        this.dbFile.seek(bytPositionOfRow + 1);
        int recordLength = this.dbFile.readInt();
        this.dbFile.seek(bytPositionOfRow + 5);

        byte[] data = new byte[recordLength];
        this.dbFile.read(data);

        return data;
    }

    /**
     * After opening the FileHandler, we load all data into the index
     * @throws IOException
     */
    public void loadAllDataToIndex() throws IOException {
        if (this.dbFile.length() == 0) {
            return;
        }
        long currentPos = 0;
        long rowNum = 0;

        while(currentPos < this.dbFile.length()) {
            this.dbFile.seek(currentPos);
            boolean isDeleted = this.dbFile.readBoolean();
            if (!isDeleted) {
                Index.getInstance().add(currentPos);
                rowNum++;
            }

            currentPos += 1;
            this.dbFile.seek(currentPos);
            int recordLength = this.dbFile.readInt();
            currentPos += 4;
            currentPos += recordLength;
        }

        System.out.println("Total row number in DB is: " + rowNum);
    }

    /**
     * Closing a db file
     * @throws IOException
     */
    public void close() throws IOException {
        this.dbFile.close();
    }

    public void deleteRow(int rowNumber) throws IOException {
        long bytePositionOfRecord = Index.getInstance().getBytePosition(rowNumber);
        if (bytePositionOfRecord == -1) {
            throw new IOException("Row doesn't exist in Index!");
        }
        this.dbFile.seek(bytePositionOfRecord);
        this.dbFile.writeBoolean(true);

        // update the index
        Index.getInstance().remove(rowNumber);
    }
}