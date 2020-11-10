package org.karnak.cache;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.karnak.ui.extid.Patient;

public abstract class PatientClient {
    private String name;
    private final HazelcastInstance hazelcastInstance;

    public PatientClient(String name, int ttlSeconds) {
        this.hazelcastInstance = Hazelcast.newHazelcastInstance(createConfig(name, ttlSeconds));
        this.name = name;
    }

    private Config createConfig(String name, int ttlSeconds) {
        Config config = new Config();
        MapConfig mapConfig = new MapConfig(name);
        mapConfig.setTimeToLiveSeconds(ttlSeconds);
        mapConfig.setMaxIdleSeconds(20);
        config.addMapConfig(mapConfig);
        config.getCPSubsystemConfig().setCPMemberCount(3);
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
}
