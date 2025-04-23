from .base_model import BaseModel
import numpy as np
from collections import Counter

def euclidean_distance(a, b):
    return np.linalg.norm(a - b)

class IterativeKNNModel(BaseModel):
    def __init__(self, input_shape, num_classes, k=15):
        super().__init__(input_shape, num_classes)
        self.k = k
        self.X_train = None
        self.y_train = None

    def build(self):
        # Pas de modèle à construire pour KNN
        pass

    def train(self, X_train, y_train, X_val=None, y_val=None):
        self.X_train = X_train
        self.y_train = y_train
        return None  # Pas d'historique d'entraînement

    def get_iterative_neighbors(self, x):
        n = len(self.X_train)
        used_indices = set()
        neighborhood = [x]
        neighbors = []

        for _ in range(self.k):
            min_dist = float("inf")
            best_idx = -1

            for i in range(n):
                if i in used_indices:
                    continue
                candidate = self.X_train[i]
                dist = min(euclidean_distance(candidate, v) for v in neighborhood)
                if dist < min_dist:
                    min_dist = dist
                    best_idx = i

            if best_idx == -1:
                break
            neighbors.append(best_idx)
            neighborhood.append(self.X_train[best_idx])
            used_indices.add(best_idx)

        return neighbors

    def predict(self, X):
        predictions = []
        for x in X:
            neighbor_indices = self.get_iterative_neighbors(x)
            # Correction : convertir les labels en entiers si ce sont des one-hot ou arrays
            neighbor_labels = [
                int(np.argmax(self.y_train[i])) if isinstance(self.y_train[i], (np.ndarray, list)) else self.y_train[i]
                for i in neighbor_indices
            ]
            vote = Counter(neighbor_labels).most_common(1)[0][0]
            # Pour compatibilité avec keras: one-hot output
            pred = np.zeros(self.num_classes)
            pred[vote] = 1
            predictions.append(pred)
        return np.array(predictions)

    def evaluate(self, X_test, y_test):
        y_pred = self.predict(X_test)
        y_pred_classes = np.argmax(y_pred, axis=1)
        y_true_classes = np.argmax(y_test, axis=1)
        accuracy = np.mean(y_pred_classes == y_true_classes)
        return None, accuracy

    def save(self, filepath):
        # Optionnel: sauvegarde possible avec pickle
        import pickle
        with open(filepath, "wb") as f:
            pickle.dump({
                "X_train": self.X_train,
                "y_train": self.y_train,
                "k": self.k,
                "input_shape": self.input_shape,
                "num_classes": self.num_classes,
            }, f)

    def load(self, filepath):
        import pickle
        with open(filepath, "rb") as f:
            data = pickle.load(f)
            self.X_train = data["X_train"]
            self.y_train = data["y_train"]
            self.k = data["k"]
            self.input_shape = data["input_shape"]
            self.num_classes = data["num_classes"]
