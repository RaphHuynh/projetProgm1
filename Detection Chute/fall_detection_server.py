import json
import numpy as np
import pandas as pd
from collections import deque
import paho.mqtt.client as mqtt
from sklearn.preprocessing import StandardScaler
from Models.cnn_model import CNNModel

# Configuration MQTT
BROKER_ADDRESS = "194.57.103.203"
BROKER_PORT = 1883
TOPIC = "vehicle"

# Configuration du traitement des données
WINDOW_SIZE = 100
data_buffer = deque(maxlen=WINDOW_SIZE)
scaler = StandardScaler()

# Chargement du modèle
model = CNNModel(input_shape=(None, 1), num_classes=2)
model.load("Models/model_cnnmodel.h5")

def process_data(data_window):
    if len(data_window) < WINDOW_SIZE:
        return None

    df = pd.DataFrame(list(data_window))
    features = []
    for col in ["ACCX", "ACCY", "ACCZ"]:
        signal = df[col].values
        features.extend([
            signal,
            np.gradient(signal),
            np.gradient(np.gradient(signal))
        ])
    
    features = np.array(features).flatten()
    features = scaler.fit_transform(features.reshape(-1, 1)).flatten()
    
    # Préparer les données pour le modèle
    model_input = np.expand_dims(features, axis=(0, -1))
    return model_input

def on_message(client, userdata, message):
    try:
        # Décoder le message JSON
        data = json.loads(message.payload.decode())
        # format des données : {"accelerometer":{"x":0,"y":9.776321,"z":0.812345},"pressure":{"pressure":1013.25},"gps":{"latitude":0,"longitude":0}} on topic vehicle

        # Extraire les données d'accélération
        acc_data = {
            "ACCX": data.get("accelerometer", {}).get("x", 0),
            "ACCY": data.get("accelerometer", {}).get("y", 0),
            "ACCZ": data.get("accelerometer", {}).get("z", 0)
        }

        print(acc_data)
        
        # Ajouter les données au buffer
        data_buffer.append(acc_data)
        
        # Traiter les données si nous avons assez d'échantillons
        if len(data_buffer) == WINDOW_SIZE:
            model_input = process_data(data_buffer)
            if model_input is not None:
                # Faire la prédiction
                prediction = model.predict(model_input)
                confidence = np.max(prediction)
                predicted_class = np.argmax(prediction)
                # Afficher les résultats
                print(f"Prédiction: {predicted_class}, Confiance: {confidence:.2%}")
                
                # Si le modèle prédit une chute avec une confiance élevée
                if predicted_class == 1 and confidence > 0.9 :  # 0 = Accident
                    print(f"⚠️ CHUTE DETECTÉE! (confiance: {confidence:.2%})")
                    # Envoyer une alerte sur le mqtt
                    
                    client.publish("alert", json.dumps({"Notification": "Chute détectée!"}))

                    
    except Exception as e:
        print(f"Erreur lors du traitement du message: {e}")

def main():
    client = mqtt.Client()
    client.on_message = on_message
    
    try:
        client.connect(BROKER_ADDRESS, BROKER_PORT, 60)
        print("Connecté au broker MQTT")
        print("En attente de données...")
        
        client.subscribe(TOPIC)
        client.loop_forever()
        
    except Exception as e:
        print(f"Erreur de connexion: {e}")

if __name__ == "__main__":
    main()
