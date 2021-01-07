package org.karnak.backend.cache;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import java.util.Collection;

public abstract class PatientClient {
    // https://docs.hazelcast.org/docs/latest/manual/html-single/#cp-subsystem
    private static final String CLUSTER_NAME = "PatientClient";
    private static final int CP_MEMBER = 3;
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
        config.setClusterName(CLUSTER_NAME);
        config.getCPSubsystemConfig().setCPMemberCount(CP_MEMBER);
        config.setClassLoader(PseudonymPatient.class.getClassLoader());
        return config;
    }

    public PseudonymPatient put(String key, PseudonymPatient patient) {
        IMap<String, PseudonymPatient> map = hazelcastInstance.getMap(name);
        return map.putIfAbsent(key, patient);
    }

    public PseudonymPatient get(String key) {
        IMap<String, PseudonymPatient> map = hazelcastInstance.getMap(name);
        return map.get(key);
    }

    public void remove(String key) {
        IMap<String, PseudonymPatient> map = hazelcastInstance.getMap(name);
        map.remove(key);
    }

    public Collection<PseudonymPatient> getAll() {
        IMap<String, PseudonymPatient> map = hazelcastInstance.getMap(name);
        return map.values();
    }
}
