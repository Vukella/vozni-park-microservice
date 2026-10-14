package com.voznipark.fleet.kafka.topics;

// topic name constants — used in KafkaTopicConfig, FleetEventProducer and consumers
public final class FleetTopics {

    // Vehicle events

    // status transition
    public static final String VEHICLE_STATUS_CHANGED          = "fleet.vehicle-status-changed";

    // inoperable=true → IN_FAULT status
    public static final String VEHICLE_FAULT_REPORTED          = "fleet.vehicle-fault-reported";

    public static final String VEHICLE_FAULT_RESOLVED          = "fleet.vehicle-fault-resolved";

    public static final String VEHICLE_SERVICE_SCHEDULED       = "fleet.vehicle-service-scheduled";

    public static final String VEHICLE_DECOMMISSIONED          = "fleet.vehicle-decommissioned";

    public static final String VEHICLE_DOCUMENT_UPLOADED       = "fleet.vehicle-document-uploaded";

    // audit entry goes to registration_change_log
    public static final String REGISTRATION_NUMBER_CHANGED     = "fleet.registration-number-changed";

    public static final String VEHICLE_ASSIGNED_DRIVER_CHANGED = "fleet.vehicle-assigned-driver-changed";

    // Driver events

    // status transition
    public static final String DRIVER_STATUS_CHANGED           = "fleet.driver-status-changed";

    // sets SUSPENDED immediately
    public static final String DRIVER_LICENSE_REVOKED          = "fleet.driver-license-revoked";

    // clears SUSPENDED
    public static final String DRIVER_LICENSE_RESTORED         = "fleet.driver-license-restored";

    public static final String TRAFFIC_ACCIDENT_REPORTED       = "fleet.traffic-accident-reported";

    // Shared / cross-entity events

    /**
     * Generalized document expiry warning covering all 9 tracked document types
     * for both vehicles and drivers. Published by StatusRefreshService during
     * the daily refresh cycle when daysUntilExpiry <= configured threshold.
     */
    public static final String DOCUMENT_EXPIRING               = "fleet.document-expiring";

    // Saga topics

    public static final String VEHICLE_RESERVED           = "fleet.vehicle.reserved";
    public static final String VEHICLE_RESERVATION_FAILED = "fleet.vehicle.reservation-failed";
    public static final String DRIVERS_RESERVED           = "fleet.drivers.reserved";
    public static final String DRIVER_RESERVATION_FAILED  = "fleet.driver.reservation-failed";
    public static final String VEHICLE_RELEASED           = "fleet.vehicle.released";

    private FleetTopics() { /* constants only — no instances */ }
}
