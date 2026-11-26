(function () {
    'use strict';

    var draggedCard = null;
    var draggedId = null;
    var draggedStatus = null;
    var dragEndedAt = 0;

    function findCard(target) {
        return target && target.closest ? target.closest('.task-card-modern') : null;
    }

    function findColumn(target) {
        return target && target.closest ? target.closest('.kanban-col') : null;
    }

    function clearDropTargets() {
        var columns = document.querySelectorAll('.kanban-col.is-drop-target');
        for (var i = 0; i < columns.length; i++) {
            columns[i].classList.remove('is-drop-target');
        }
    }

    function resetDragState() {
        if (draggedCard) {
            draggedCard.classList.remove('is-dragging');
        }
        clearDropTargets();
        draggedCard = null;
        draggedId = null;
        draggedStatus = null;
    }

    function prepararCards() {
        var cards = document.querySelectorAll('.task-card-modern');
        for (var i = 0; i < cards.length; i++) {
            cards[i].setAttribute('draggable', 'true');
        }
    }

    function iniciar() {
        prepararCards();
        var jq = window.jQuery || (window.PrimeFaces && window.PrimeFaces.jQuery);
        if (jq) {
            jq(document).on('pfAjaxComplete', prepararCards);
        }
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', iniciar);
    } else {
        iniciar();
    }

    document.addEventListener('dragstart', function (event) {
        var card = findCard(event.target);
        if (!card) {
            return;
        }

        var idElement = card.querySelector('[data-tarefa-id]');
        if (!idElement) {
            return;
        }

        draggedCard = card;
        draggedId = idElement.getAttribute('data-tarefa-id');
        draggedStatus = idElement.getAttribute('data-tarefa-status');

        if (event.dataTransfer) {
            event.dataTransfer.effectAllowed = 'move';
            event.dataTransfer.clearData();
            event.dataTransfer.setData('text/plain', draggedId);
            if (event.dataTransfer.setDragImage) {
                event.dataTransfer.setDragImage(card, 20, 20);
            }
        }

        card.classList.add('is-dragging');
    });

    document.addEventListener('dragover', function (event) {
        if (!draggedId) {
            return;
        }

        // Permite o drop em qualquer lugar enquanto um card e arrastado,
        // evitando que o navegador trate o link como uma URL.
        event.preventDefault();

        if (event.dataTransfer) {
            event.dataTransfer.dropEffect = 'move';
        }

        var column = findColumn(event.target);
        if (!column) {
            return;
        }

        var columns = document.querySelectorAll('.kanban-col.is-drop-target');
        for (var i = 0; i < columns.length; i++) {
            if (columns[i] !== column) {
                columns[i].classList.remove('is-drop-target');
            }
        }
        column.classList.add('is-drop-target');
    });

    document.addEventListener('dragleave', function (event) {
        var column = findColumn(event.target);
        if (column && !column.contains(event.relatedTarget)) {
            column.classList.remove('is-drop-target');
        }
    });

    document.addEventListener('drop', function (event) {
        if (!draggedId) {
            return;
        }

        event.preventDefault();

        var column = findColumn(event.target);
        var movedId = draggedId;
        var sourceStatus = draggedStatus;
        var targetStatus = column ? column.getAttribute('data-status') : null;

        resetDragState();
        dragEndedAt = Date.now();

        if (!column || !targetStatus || targetStatus === sourceStatus) {
            return;
        }

        if (typeof window.moverTarefaKanban === 'function') {
            window.moverTarefaKanban([
                { name: 'tarefaId', value: movedId },
                { name: 'status', value: targetStatus }
            ]);
        }
    });

    document.addEventListener('dragend', function () {
        resetDragState();
        dragEndedAt = Date.now();
    });

    // Evita que um clique disparado logo apos o drag abra a tarefa.
    document.addEventListener('click', function (event) {
        if (Date.now() - dragEndedAt < 300 && findCard(event.target)) {
            event.preventDefault();
            event.stopPropagation();
        }
    }, true);
})();
