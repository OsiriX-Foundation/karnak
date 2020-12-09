package org.karnak.cache;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.Collection;

public abstract class PatientClient {
    // https://docs.hazelcast.org/docs/latest/manual/html-single/#cp-subsystem
    private final static int CPMember = 3;
    private final static String cluserName = "PatientClient";
    private final String name;
    private final HazelcastInstance hazelcastInstance;

    public PatientClient(String name, int ttlSeconds) {
        this.name = name;
        this.hazelcastInstance = Hazelcast.newHazelcastInstance(createConfig(ttlSeconds));
    }

    private Config createConfig(int ttlSeconds) {
        Config config = new Config();
        MapConfig mapConfig = new MapConfig(name);
        mapConfig.setTimeToLiveSeconds(ttlSeconds);
        // The method setMaxIdleSeconds defines how long the entry stays in the cache without being touched
        // mapConfig.setMaxIdleSeconds(20);
        config.addMapConfig(mapConfig);
        config.setClusterName(cluserName);
        config.getCPSubsystemConfig().setCPMemberCount(CPMember);
        config.setClassLoader(Patient.class.getClassLoader());
        return config;
    }

    public Patient put(String key, Patient patient) {
        IMap<String, Patient> map = hazelcastInstance.getMap(name);
        return map.putIfAbsent(key, patient);
    }

    public Patient get(String key) {
        IMap<String, Patient> map = hazelcastInstance.getMap(name);
        return map.get(key);
    }

    public void remove(String key) {
        IMap<String, Patient> map = hazelcastInstance.getMap(name);
        map.remove(key);
    }

    public Collection<Patient> getAll() {
        IMap<String, Patient> map = hazelcastInstance.getMap(name);
        return map.values();
    }
}
