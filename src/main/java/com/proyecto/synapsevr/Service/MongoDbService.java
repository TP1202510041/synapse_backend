package com.proyecto.synapsevr.Service;

import com.proyecto.synapsevr.Service.ServiceImpl.MongoDbServiceImpl;

import java.util.List;
import java.util.UUID;

public interface MongoDbService {

    /**
     * Guarda los datos de HeartRate en MongoDB
     * @param heartRateRecords Lista de registros de frecuencia cardíaca
     * @param sessionId ID de la sesión
     * @param patientId ID del paciente
     * @return ID del documento creado en MongoDB
     */
    String saveHeartRateData(List<Object> heartRateRecords, UUID sessionId, Integer patientId);

    /**
     * Elimina los datos de HeartRate de MongoDB
     * @param mongoDocumentId ID del documento en MongoDB
     */
    void deleteHeartRateData(String mongoDocumentId);

    /**
     * Obtiene los datos de HeartRate desde MongoDB
     * @param mongoDocumentId ID del documento en MongoDB
     * @return Documento con los datos de HeartRate
     */
    MongoDbServiceImpl.HeartRateDocument getHeartRateData(String mongoDocumentId);
}