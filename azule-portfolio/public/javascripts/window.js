function closeStartMenu() {
    $('.task-bar-start').removeClass('window-button-clicked');
    $('.start-menu').empty();
    $('.start-menu').addClass('window-hidden');
}

function registerLoginForm() {
    $('#login-form').submit(function(e) {
        e.preventDefault();
        var form = $(this);
        var btn = form.find('#login-page-submit');
        console.log(form.serialize());
        jsRoutes.controllers.UserController.login().ajax({
            method: 'POST',
            data: form.serialize(),
            success: function(data) {
                console.log(data);
                btn.attr('disabled', true);
            },
            error: function(data) {
                console.log(data);
                btn.attr('disabled', false);
            }
        });
    });
}

function insertStartMenu(data) {
    var main = $('.start-menu');
    main.empty();
    main.append(data);
    main.removeClass('window-hidden');
}

function getMaxZ() {
    return Math.max.apply(1, $('.window').map(function(){
        return $(this).css('z-index');
    }));
}

function setZIndex(thisWindow) {
    var maxZ = getMaxZ();
    if(thisWindow.css('z-index') < maxZ) {
        var thisWindowFlag = false;
        $('.window').sort(function(a, b){
            return ($(a).css('z-index') < $(b).css('z-index') ? 1 : -1);
        }).each(function(){
            if($(this).css('z-index') === thisWindow.css('z-index')) {
                $(this).css('z-index', maxZ);
                thisWindowFlag = true;
            } else if(!thisWindowFlag) {
                $(this).css('z-index', $(this).css('z-index') - 1);
            }
        });
    }
}

function loadWindow(w) {
    var taskId = '#task-bar-' + w.attr('id');

    w.resizable({
        handles: "all",
        minHeight: 300,
        minWidth: 300,
    }).draggable({
        handle: '.window-title'
    });
    w.mousedown(function(){
        setZIndex(w);
    });
    w.find('.window-title').mousedown(function(e){
        setZIndex(w);
        if(w.hasClass('window-fullscreen')) {
            w.removeClass('window-fullscreen');
            w.css('top', e.clientY - 20);
            w.css('left', e.clientX - w.innerWidth() / 2);
        }
    });
    w.find('.window-button-close').click(function(){
        w.remove();
        $(taskId).remove();
    });
    w.find('.window-button-full-window').click(function(){
        if(w.hasClass('window-fullscreen')) {
            w.removeClass('window-fullscreen');
        } else {
            w.addClass('window-fullscreen');
        }
    });
    w.find('.window-button-taskbar').click(function(){
        w.addClass('window-hidden');
        $(taskId).addClass('task-bar-hidden-window');
    });
    $(taskId).click(function(){
        var task = $(this);
        if(task.hasClass('task-bar-hidden-window')) {
            w.removeClass('window-hidden');
            task.removeClass('task-bar-hidden-window');
        } else {
            w.addClass('window-hidden');
            task.addClass('task-bar-hidden-window');
        }
    });
}

function taskElement(windowId, icon, windowName) {
    return '<button id="task-bar-' + windowId + '" class="task-bar-app"> <i class="bi ' + icon + '"></i> ' + windowName + ' </button>'
}

function showWindow(data) {
    var lastAddedWindow = $('.window:last-of-type');
    $('.main-windows').append(data);
    var addWindow = $('.window:last-of-type');
    addWindow.css('z-index', getMaxZ() + 1);
    if(!lastAddedWindow[0] || lastAddedWindow.offset().left + addWindow.width() + 80 >= $(window).width() || lastAddedWindow.offset().top + addWindow.height() + 80 >= $(window).height()) {
        addWindow.css('top', '40px');
        addWindow.css('left', '140px');
    } else {
        addWindow.css('top', lastAddedWindow.offset().top + 40);
        addWindow.css('left', lastAddedWindow.offset().left + 40);
    }
    $('.task-bar-content').append(taskElement(addWindow.attr('id'), 'bi-folder-fill', addWindow.find('.window-title-text').text()));
    loadWindow(addWindow);
}

function registerDblclick(id, url) {
    $('#icon-' + id).dblclick(function(){
        if(!$('#window-' + id)[0]) {
            url.ajax({
                success: function(data) {
                    showWindow(data);
                },
                error: function(data) {
                    showWindow(data);
                }
            });
        } else {
            var thisWindow = $('#window-' + id);
            var task = $('#task-bar-' + thisWindow.attr('id'));
            setZIndex(thisWindow);
            if(task.hasClass('task-bar-hidden-window')) {
                thisWindow.removeClass('window-hidden');
                task.removeClass('task-bar-hidden-window');
            }
        }
    });
}

$(function(){
    $('.task-bar-start').click(function(){
        if($('.start-menu').hasClass('window-hidden')) {
            $(this).addClass('window-button-clicked');
            jsRoutes.controllers.UserController.control().ajax({
                method: 'GET',
                success: function(data) {
                    insertStartMenu(data);
                    registerDblclick('manageProduction', jsRoutes.controllers.Productions.index());
                },
                error: function(data) {
                    insertStartMenu(data.responseText);
                    registerLoginForm();
                }
            });
        } else {
            closeStartMenu();
        }
    });

    $(document).click(function(e){
        if(!$('.start-menu').hasClass('window-hidden') && !$(e.target).closest('.start-menu')[0] && !$(this).hasClass('task-bar-start') && $(this).hasClass('start-menu-content')) {
            closeStartMenu();
        }
    });

    $('.window').each(function(){
        $('.task-bar-content').append(taskElement($(this).attr('id'), 'bi-folder-fill', $(this).find('.window-title-text').text()));
    });

    registerDblclick('manual', jsRoutes.controllers.IntroductionController.manual());
    registerDblclick('work', jsRoutes.controllers.IntroductionController.work());
    registerDblclick('about', jsRoutes.controllers.IntroductionController.about());

    $('.display-icon').on('selectstart', function(){
        return false;
    });
    $('.start-menu-content').on('selectstart', function(){
        return false;
    });

    loadWindow($('.window'));
});