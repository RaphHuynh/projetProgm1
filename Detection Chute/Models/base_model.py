from abc import ABC, abstractmethod

class BaseModel(ABC):
    def __init__(self, input_shape, num_classes):
        self.input_shape = input_shape
        self.num_classes = num_classes
        self.model = None

    @abstractmethod
    def build(self):
        pass

    @abstractmethod
    def train(self, X_train, y_train, X_val=None, y_val=None):
        pass

    @abstractmethod
    def evaluate(self, X_test, y_test):
        pass

    @abstractmethod
    def predict(self, X):
        pass

    @abstractmethod
    def save(self, filepath):
        pass

    @abstractmethod
    def load(self, filepath):
        pass

    def get_input_length(self):
        if self.input_shape:
            return self.input_shape[0]
        elif self.model is not None:
            return self.model.input_shape[1]
        else:
            return None
