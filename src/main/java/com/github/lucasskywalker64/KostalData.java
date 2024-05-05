package com.github.lucasskywalker64;

import de.spyderscript.inverter.client.InverterClient;
import de.spyderscript.inverter.register.InverterAddress;
import de.spyderscript.modbus.tcp.ModbusException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.tinylog.Logger;

/**
 * The KostalData class manages the fetching of data from the Kostal inverter.
 * <p/>It connects to the inverter, retrieves data, and provides methods to access and clear
 * the fetched data.
 */
public class KostalData {

  private static final ArrayList<Integer> dcInput1 = new ArrayList<>();
  private static final ArrayList<Integer> dcInput2 = new ArrayList<>();
  private static final ArrayList<Integer> batteryCharge = new ArrayList<>();
  private static final ArrayList<Integer> consFromPV = new ArrayList<>();
  private static final ArrayList<Integer> consFromBattery = new ArrayList<>();
  private static final ArrayList<Integer> gridPurchase = new ArrayList<>();
  private static final ArrayList<Integer> gridFeedIn = new ArrayList<>();
  private static final ArrayList<Timestamp> timestamp = new ArrayList<>();

  private final InverterClient client;

  /**
   * Retrieves the fetched data and clears the internal lists.
   *
   * @return A map containing the fetched data.
   */
  public Map<String, ArrayList<Object>> getDataAndClear() {
    HashMap<String, ArrayList<Object>> listMap = new HashMap<>();
    listMap.put("dcInput1", new ArrayList<>(dcInput1));
    listMap.put("dcInput2", new ArrayList<>(dcInput2));
    listMap.put("batteryCharge", new ArrayList<>(batteryCharge));
    listMap.put("consFromPV", new ArrayList<>(consFromPV));
    listMap.put("consFromBattery", new ArrayList<>(consFromBattery));
    listMap.put("gridPurchase", new ArrayList<>(gridPurchase));
    listMap.put("gridFeedIn", new ArrayList<>(gridFeedIn));
    listMap.put("timestamp", new ArrayList<>(timestamp));
    dcInput1.clear();
    dcInput2.clear();
    batteryCharge.clear();
    consFromPV.clear();
    consFromBattery.clear();
    gridPurchase.clear();
    gridFeedIn.clear();
    timestamp.clear();
    return listMap;
  }

  /**
   * Fetches new data from the Kostal inverter and updates the internal lists.
   */
  public void fetchNewData() {
    try {
      client.connect();
      timestamp.add(new Timestamp(System.currentTimeMillis()));
      int dc1 = (int) ((float) client.read(InverterAddress.POWER_DC1).getData());
      dcInput1.add(Math.max(dc1, 0));
      int dc2 = (int) ((float) client.read(InverterAddress.POWER_DC2).getData());
      dcInput2.add(Math.max(dc2, 0));
      batteryCharge.add(Integer.parseInt(client.read(InverterAddress.BATTERY_ACTUAL_SOC).getData()
          .toString()));
      consFromPV.add((int) ((float) client.read(InverterAddress.HOME_OWN_CONSUMPTION_FROM_PV)
          .getData()));
      consFromBattery.add((int) ((float) client.read(
          InverterAddress.HOME_OWN_CONSUMPTION_FROM_BATTERY).getData()));

      // positive = purchase, negative = feed-in
      int grid = (int) ((float) client.read(InverterAddress.HOME_OWN_CONSUMPTION_FROM_GRID)
          .getData());
      if (grid >= 0) {
        gridPurchase.add(grid);
        gridFeedIn.add(0);
      } else {
        gridPurchase.add(0);
        gridFeedIn.add(-grid);
      }

      client.disconnect();
    } catch (IOException | ModbusException e) {
      Logger.error(e.getMessage(), e);
    }
  }

  /**
   * Constructs a KostalData object using the provided properties.
   *
   * @param properties The properties containing host and port information for the inverter.
   * @throws UnknownHostException If the host IP address cannot be determined.
   */
  public KostalData(Properties properties) throws UnknownHostException {
    client = new InverterClient(new InetSocketAddress(InetAddress
        .getByName(properties.getProperty("hostInverter")),
        Integer.parseInt(properties.getProperty("portInverter"))),
        (byte) 71);
  }
}
