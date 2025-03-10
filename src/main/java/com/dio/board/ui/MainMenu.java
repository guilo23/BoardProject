package com.dio.board.ui;

import com.dio.board.persistence.entity.Board;
import com.dio.board.persistence.entity.BoardColumnEntity;
import com.dio.board.persistence.entity.BoardUnlockKindEnum;
import com.dio.board.service.BoardQueryService;
import com.dio.board.service.BoardService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.dio.board.persistence.config.ConnectionConfig.getConnection;

public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);

    public void execute()throws SQLException{
        System.out.println("Bem vindo ao gerenciador de Boards, escolha a opção desejada");
        var option = -1;
        while (true){
            System.out.println("1 - Criar um novo Board");
            System.out.println("2 - Selecionar um Board Existente");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Sair");
            option = scanner.nextInt();
            switch (option){
                case 1 -> createdBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(1);
                default -> System.out.println("Opção inválida, escolha uma opção válida");

            }
        }
    }

    private void deleteBoard() throws SQLException{
        System.out.println("Informe o ID que deseja excluir");
        var id =scanner.nextLong();
        try (var connection = getConnection()){
            var service = new BoardService(connection);
            if (service.delete(id)){
                System.out.printf("O Board %s foi Excluido",id);
            } else {
                System.out.printf("não foi encontrado o Board com id: %s",id);
            }
        }
    }

    private void selectBoard() throws SQLException{
        System.out.println("informe o ID que deseja acessar");
        var id = scanner.nextLong();
        try (var connection = getConnection()){
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(
                    b -> new BoardMenu(b).execute(),
                    () ->System.out.printf("não foi encontrado o Board com id: %s",id));
        }
    }

    private void createdBoard() throws SQLException {
        var entity = new Board();
        System.out.println("Informe o nome da Board");
        entity.setName(scanner.next());
        System.out.println("Seu Board terá colunas além de 3 ? se sim informe quantas, senão digite '0'");
        var addtionalColumn = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();
        System.out.println("Informe o nome da coluna inicial do board");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName,BoardUnlockKindEnum.INITIAL,0);
        columns.add(initialColumn);
        for(int i = 0; i < addtionalColumn; i++){
            System.out.println("Informe o nome da coluna da tarefa pendente");
            var peddingColumnName = scanner.next();
            var peddingColumn = createColumn(peddingColumnName,BoardUnlockKindEnum.PENDING,i + 1);
            columns.add(peddingColumn);
        }
        System.out.println("Informe o nome da coluna da coluna final");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName,BoardUnlockKindEnum.FINAL,addtionalColumn + 1);
        columns.add(finalColumn);

        System.out.println("Informe o nome da coluna da coluna de cancelamento");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(finalColumnName,BoardUnlockKindEnum.CANCEL,addtionalColumn + 2);
        columns.add(cancelColumn);

        entity.setBoardColumnEntities(columns);
        try (var connection = getConnection()){
            var service = new BoardService(connection);
            service.insert(entity);
        }

    }
    private BoardColumnEntity createColumn(final String name, final BoardUnlockKindEnum kind, final int order){
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setKind(kind);
        boardColumn.setOrder(order);

        return  boardColumn;
    }
}
