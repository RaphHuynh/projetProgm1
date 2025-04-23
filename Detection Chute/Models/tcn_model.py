from tensorflow.keras.models import Sequential, load_model as keras_load_model
from tensorflow.keras.layers import Dense, Dropout, BatchNormalization, Flatten
from tensorflow.keras.callbacks import EarlyStopping, ReduceLROnPlateau
from tcn import TCN
from .base_model import BaseModel

class TCNModel(BaseModel):
    def build(self):
        self.model = Sequential([
            TCN(input_shape=self.input_shape),
            BatchNormalization(),
            Dropout(0.3),
            Dense(128, activation="relu"),
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
        self.model = keras_load_model(filepath, custom_objects={"TCN": TCN})
        self.input_shape = self.model.input_shape[1:]
