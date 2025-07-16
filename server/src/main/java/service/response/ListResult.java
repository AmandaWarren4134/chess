package service.response;

import Model.GameData;

import java.util.ArrayList;

public record ListResult(ArrayList<GameData> gameList) { }
