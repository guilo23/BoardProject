package com.dio.board.ui;

import com.dio.board.persistence.dto.BoardColumnInfoDTO;
import com.dio.board.persistence.entity.Board;
import com.dio.board.persistence.entity.BoardColumnEntity;
import com.dio.board.persistence.entity.BoardUnlockKindEnum;
import com.dio.board.persistence.entity.CardEntity;
import com.dio.board.service.BoardColumnQueryService;
import com.dio.board.service.BoardQueryService;
import com.dio.board.service.CardQueryService;
import com.dio.board.service.CardService;
import lombok.AllArgsConstructor;

import java.sql.SQLException;
import java.util.Scanner;

import static com.dio.board.persistence.config.ConnectionConfig.getConnection;

@AllArgsConstructor
public class BoardMenu {

    private final Board entity;


    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    public void execute() {
        try {
            System.out.println("Bem vindo ao board desejado, Escolha uma opção");
            var option = -1;
            while (option != 9) {
                System.out.println("1 - Criar um card");
                System.out.println("2 - mover um Card ");
                System.out.println("3 - bloquear um card");
                System.out.println("4 - desbloquear um card");
                System.out.println("5 - cancelar um card");
                System.out.println("6 - ver board");
                System.out.println("7 - ver coluna com cards");
                System.out.println("8 - ver card");
                System.out.println("9 - voltar para o menu anterior");
                System.out.println("10 - sair");
                option = scanner.nextInt();
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCard();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> CancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Retornando ao menu anterior");
                    case 10 -> System.exit(1);
                    default -> System.out.println("Opção inválida, escolha uma opção válida");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.exit(0);
        }
    }

    private void showCard() throws SQLException{
        System.out.println("Escolha o id do Card que deseja visualizar");
        var selectedCardId = scanner.nextLong();
        try (var connection = getConnection()){
            new CardQueryService(connection).findById(selectedCardId).
                    ifPresentOrElse( c -> {
                                System.out.printf("Card %s - %s.\n", c.id(), c.title());
                                System.out.printf("Descrição: %s\n", c.description());
                                System.out.println(c.blocked() ?
                                        "Está bloqueado. Motivo: " + c.blockReason() :
                                        "Não está bloqueado");
                                System.out.printf("Já foi bloqueado %s vezes\n", c.blockAmount());
                                System.out.printf("Está no momento na coluna %s - %s\n", c.columnId(), c.columnName());
                            },
                            () -> System.out.printf("Não existe um card com o id %s\n", selectedCardId));
        }
    }

    private void showColumn() throws SQLException {
        var columnsIds = entity.getBoardColumnEntities().stream().map(BoardColumnEntity::getId).toList();
        var selectedColumnId = -1L;
        while (!columnsIds.contains(selectedColumnId)) {
            System.out.printf("Escolha uma coluna do board %s pelo id\n", entity.getName());
            entity.getBoardColumnEntities().forEach(c -> System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));
            selectedColumnId = scanner.nextLong();
        }
        try (var connection = getConnection()) {
            var column = new BoardColumnQueryService(connection).findById(selectedColumnId);
            column.ifPresent(co -> {
                System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                co.getCards().forEach(ca -> System.out.printf("Card %s - %s\nDescrição: %s",
                        ca.getId(), ca.getTitle(), ca.getDescription()));
            });
        }
    }

    private void showBoard() throws SQLException {
        try (var connection = getConnection()) {
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.ifPresent(b -> {
                System.out.printf("Board [%s,%s]\n", b.id(), b.name());
                b.columns().forEach(c ->
                        System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n", c.name(), c.kind(), c.cardsAmount())
                );
            });
        }
    }

    private void CancelCard() throws SQLException{
        System.out.println("Informe o id do card que deseja mover para a coluna de cancelamento");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsInfo = entity.getBoardColumnEntities().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void unblockCard() throws SQLException{
        System.out.println("informe o id do card que será desbloqueado");
        var cardId = scanner.nextLong();
        System.out.println("informe o motivo do desbloqueio do card");
        var reason = scanner.next();
        var boardColumnsInfo = entity.getBoardColumnEntities().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).unblock(cardId,reason);
        }catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }

    }

    private void blockCard() throws SQLException{
        System.out.println("informe o id do card que será bloqueado");
        var cardId = scanner.nextLong();
        System.out.println("informe o motivo do bloqueio do card");
        var reason = scanner.next();
        var boardColumnsInfo = entity.getBoardColumnEntities().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).block(cardId,reason,boardColumnsInfo);
        }catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void moveCard() throws SQLException{
        System.out.println("Digite o id do card que deseja mover");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumnEntities().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).cardToNextColumn(cardId,boardColumnsInfo);
        }

    }

    private void createCard()throws SQLException {
        var card = new CardEntity();
        System.out.println("informe o titulo do card");
        card.setTitle(scanner.next());
        System.out.println("Informe a descrição");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getinitialColumn());
        try (var connection = getConnection()){
                new CardService(connection).create(card);
        }
    }
}
