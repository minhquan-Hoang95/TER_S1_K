package com.example.demo.models.entities;


import com.example.demo.models.components.maze.Cell;
import com.example.demo.models.components.maze.Grid;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Document(collection = "mazes")
public class MazeEntity {

    @Id
    private String id;
    private int rows;
    private int cols;
    private String algorithm;
    private List<List<Map<String, Boolean>>> cells;
    private Integer rating; // 0 to 5

    public MazeEntity() {
    }

    public MazeEntity(Grid grid, String algorithm) {
        this.rows = grid.getRows();
        this.cols = grid.getColumns();
        this.algorithm = algorithm;

        this.cells = new ArrayList<>();

        for (int r = 0; r < grid.getRows(); r++) {
            List<Map<String, Boolean>> rowList = new ArrayList<>();
            for (int c = 0; c < grid.getColumns(); c++) {
                Cell cell = grid.getCell(r, c);
                Map<String, Boolean> cellMap = new LinkedHashMap<>();

                cellMap.put("north", cell.north == null || !cell.isLinked(cell.north));
                cellMap.put("east",  cell.east == null  || !cell.isLinked(cell.east));
                cellMap.put("south", cell.south == null || !cell.isLinked(cell.south));
                cellMap.put("west",  cell.west == null  || !cell.isLinked(cell.west));

                rowList.add(cellMap);
            }
            this.cells.add(rowList);
        }

    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
