package guru.qa.niffler.jupiter.extension.spend;

import guru.qa.niffler.model.SpendJson;

public abstract class SpendExtension {

    abstract SpendJson create(SpendJson spend);
}
