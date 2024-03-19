package service;

import dao.Fruit;

public interface FruitService {
    int saveFruit(Fruit fruit);
    Fruit getFruit(int id);
}
