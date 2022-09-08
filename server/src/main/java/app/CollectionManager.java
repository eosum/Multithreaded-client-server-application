package app;

import data.Car;
import data.Coordinates;
import data.HumanBeing;
import data.WeaponType;
import database.DatabaseManager;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * CollectionManager Class
 *
 * managing a collection of items
 */
public class CollectionManager {
    CopyOnWriteArrayList<HumanBeing> collection = new CopyOnWriteArrayList<>();
    private final DatabaseManager databaseManager = new DatabaseManager();
    private Long id = 1L;
    ZonedDateTime initTime = ZonedDateTime.now();


    /**
     * Add an element to collection
     *
     * @param element element that is needed to add
     */
    public boolean add(HumanBeing element, String owner) {
        Long id = databaseManager.add(element, owner);
        if (id != null) {
            element.setId(id);
            collection.add(element);
            return true;
        }
        return false;
    }

    /**
     * Add an element to collection if it is smaller than the smallest existing one
     *
     * @param element element is needed to add
     */
    public boolean addIfMin(HumanBeing element, String owner) {
        Collections.sort(collection);
        if (collection.isEmpty() || collection.get(0).compareTo(element) >= 0) {
            Long id = databaseManager.add(element, owner);
            if (id != null) {
                element.setId(id);
                collection.add(element);
                return true;
            }
        }
        return false;
    }

    /**
     * Clear the collection
     */
    public boolean clear(String user) {
        for (HumanBeing element: collection) {
            int number = databaseManager.remove(element.getId(), user);
            if (number == -1) {
                return false;
            }
            else if (number > 0){
                collection.remove(element);
            }
        }
        return true;
    }

    /**
     * Count the number of elements with smaller value of weapon type
     *
     * @param weaponType the value to be compared with
     */
    public String countLessThanWeaponType(String weaponType) {
        int amount = 0;
        for(HumanBeing element: collection) {
            if (element.getWeaponType().length() < weaponType.length()) {
                amount++;
            }
        }
        String result = "Количество элементов = " + amount;
        return result;
    }

    /**
     * Find elements with given substring and print them
     *
     * @param subString substring to be found
     */
    public LinkedList<? extends Serializable> filterStartsWithSoundtrackName(String subString) {
        return (LinkedList<? extends Serializable>) collection.stream()
                .filter(element -> element.getSoundtrackName().startsWith(subString));
    }

    /**
     *
     * Print an information about collection
     */
    public String info() {
        String result = " - команда выполнена успешно. '\n' Тип - " + collection.getClass() + "\n"
                + "Количество элементов - " + collection.size() + "\n"
                + "Дата инициализации - " + initTime;
        return result;
    }

    /**
     * Print the collection in ascending order
     */
    public LinkedList<? extends Serializable> printAscending() {
        return (LinkedList<? extends Serializable>) collection.stream().sorted(HumanBeing::compareTo);
    }

    /**
     *
     * @param deleteID id to delete the item by
     */
    public String removeById(Long deleteID, String user) {
        int number = databaseManager.remove(deleteID, user);
        String result = "Успешное удаление элемента с ID = " + deleteID;
        if (number == -1) {
            result = deleteID + " - ошибка удаления. Попробуйте позже";
        }
        else if (number == 0) {
            result = deleteID + " - элемент не был удален, т.к. нет прав или его нет в коллекции";
        }
        else {
            collection.removeIf(element -> element.getId() == deleteID);
        }
        return result;
    }

    /**
     * Remove the first element in collection
     */
    public String removeFirst(String user) {
        String result = "Команда успешно выполнена";
        if(!collection.isEmpty()) {
            int number = databaseManager.remove(collection.get(0).getId(), user);
            if (number == -1) {
                result = "Ошибка удаления. Попробуйте позже";
            }
            else if (number == 0) {
                result = "Нет прав на удаление объекта";
            }
            else  if (number > 0) {
                collection.remove(collection.get(0));
            }
        }
        return result;
    }

    /**
     * Remove elements that greater than given one
     *
     * @param CompareElement the element to compare with
     */
    public String removeGreater(HumanBeing CompareElement, String user) {
        String result = "Команда выполнена успешно";
        for (HumanBeing element: collection) {
            if (element.compareTo(CompareElement) > 0) {
                int number = databaseManager.remove(element.getId(), user);
                if (number == -1) {
                    result = "Ошибка удаления. Попробуйте позже";
                    return result;
                } else if (number > 0) {
                    collection.remove(element);
                }
            }
        }
        return result;
    }

    /**
     * Print the collection
     */
    public synchronized LinkedList<? extends Serializable> show() {
        LinkedList<HumanBeing> result = new LinkedList<>();
        for(HumanBeing element: collection) {
            result.add(element);
        }
        return result;
    }

    /**
     * Updating element's data
     *
     * @param updateID id to update the item by
     */
    public String updateById(Long updateID, HumanBeing object, String user) {
        String result = "Команда выполнена успешно";
        int number = databaseManager.update(object, updateID, user);
        if (number == -1) {
            result = updateID + " - ошибка обновления. Попробуйте позже";
        }
        else if (number == 0) result = "Объекта нет в коллекции или у вас недостаточно прав для его изменения";
        else if (number > 0){
            object.setId(updateID);
            collection.removeIf(element -> element.getId() == updateID);
            collection.add(object);
        }
        return result;
    }

    public boolean collection_initialization() {
        ResultSet collectionFromDB = databaseManager.getFromDB();
        if (collectionFromDB == null) return false;
        try {
            while(collectionFromDB.next()) {
                HumanBeing element = new HumanBeing(
                        collectionFromDB.getLong("id"),
                        collectionFromDB.getString("name"),
                        new Coordinates(collectionFromDB.getFloat("coordinate_x"),
                                        collectionFromDB.getFloat("coordinate_y")),
                        collectionFromDB.getBoolean("real_hero"),
                        collectionFromDB.getBoolean("has_toothpick"),
                        collectionFromDB.getLong("impact_speed"),
                        collectionFromDB.getString("soundtrack"),
                        collectionFromDB.getInt("minutes_of_waiting"),
                        WeaponType.getWeaponType(collectionFromDB.getString("weapon_type")),
                        new Car(collectionFromDB.getString("car"))
                );
                collection.add(element);
            }
            return true;
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }



}

