from .base_model import BaseModel
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, BatchNormalization, Dropout
from tensorflow.keras.callbacks import EarlyStopping, ReduceLROnPlateau
import numpy as np

# Correction : import dynamique pour éviter les problèmes d'import au démarrage du script
class InceptionTimeModel(BaseModel):
    def build(self):
        # Import ici pour garantir que l'environnement est prêt et éviter les faux positifs
        try:
            from tsai.models.InceptionTime import InceptionTime
        except ImportError:
            raise ImportError("tsai n'est pas installé. Installez-le avec 'pip install tsai'.")
        self.model = Sequential([
            InceptionTime(self.input_shape, nb_classes=self.num_classes),
            BatchNormalization(),
            Dropout(0.5),
            Dense(self.num_classes, activation="softmax"),
        ])
        self.model.compile(optimizer="adam", loss="categorical_crossentropy", metrics=["accuracy"])

    def train(self, X_train, y_train, X_val=None, y_val=None):
        callbacks = [
            EarlyStopping(monitor="val_loss", patience=15, restore_best_weights=True),
            ReduceLROnPlateau(monitor="val_loss", factor=0.1, patience=5, min_lr=1e-6),
        ]
        validation_data = (X_val, y_val) if X_val is not None and y_val is not None else None
        return self.model.fit(
            X_train, y_train,
            epochs=5,
            batch_size=4,
            validation_split=0.2 if validation_data is None else 0.0,
            validation_data=validation_data,
            callbacks=callbacks,
            verbose=1,
        )

    def evaluate(self, X_test, y_test):
        return self.model.evaluate(X_test, y_test, verbose=0)

    def predict(self, X):
        return self.model.predict(X, verbose=0)

    def save(self, filepath):
        self.model.save(filepath)

    def load(self, filepath):
        from tensorflow.keras.models import load_model
        try:
            from tsai.models.InceptionTime import InceptionTime
        except ImportError:
            raise ImportError("tsai n'est pas installé. Installez-le avec 'pip install tsai'.")
        self.model = load_model(filepath, custom_objects={"InceptionTime": InceptionTime})
        self.input_shape = self.model.input_shape[1:]
