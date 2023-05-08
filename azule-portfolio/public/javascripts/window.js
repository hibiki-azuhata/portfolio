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
        jsRoutes.controllers.UserController.login().ajax({
            method: 'POST',
            data: form.serialize(),
            success: function(data) {
                closeStartMenu();
                loginInfo();
            },
            error: function(data) {
                if(data.status != 401) {
                    $('.start-menu').empty().append(data.responseText);
                    registerLoginForm();
                    registerLoginDemo();
                }
            }
        });
    });
}

function registerLoginDemo() {
    $('#login-dummy-user').click(function() {
        $.ajax({
            url: $(this).data('url'),
            method: 'POST',
            success: function(data) {
                closeStartMenu();
                loginInfo();
            },
            error: function(data) {
                if(data.status != 401) {
                    $('.start-menu').empty().append(data);
                }
            }
        });
    });
}

function registerLogoutForm() {
    $('#icon-logout').dblclick(function(e) {
        e.preventDefault();
        var form = $('#logout-form');
        jsRoutes.controllers.UserController.logout().ajax({
            method: 'POST',
            data: form.serialize(),
            success: function(data) {
                closeStartMenu();
            },
            error: function(data) {
                if(data.status != 401) {
                    $('.start-menu').empty().append(data);
                }
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

function getTaskId(w) {
    return '#task-bar-' + w.attr('id');
}

function closeWindow(w) {
    w.remove();
    $(getTaskId(w)).remove();
}

function loadWindow(w) {
    var taskId = getTaskId(w);

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
        closeWindow(w);
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
    return w;
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
    return loadWindow(addWindow);
}

function reloadWindow(_data, registerAction) {
    var data = $(_data);
    var id = data.attr('id');
    var targetWindow = $('#' + id);
    targetWindow.find('.main-window-content').remove();
    targetWindow.find('.window-menu').after(data.find('.main-window-content'));
    if(registerAction != undefined) registerAction();
}

function reloadManageUser(registerAction) {
    jsRoutes.controllers.UserController.index().ajax({
        success: function(data) { reloadWindow(data, registerAction); },
        error: function(data) {
            if(data.status != 401) {
                reloadWindow(data, registerAction);
            }
        }
    });
}

function reloadManageProduction(registerAction) {
    jsRoutes.controllers.Productions.index().ajax({
        success: function(data) { reloadWindow(data); },
        error: function(data) {
            if(data.status != 401) {
                reloadWindow(data, registerAction);
            }
        }
    });
}

function reloadManageImage(registerAction) {
    jsRoutes.controllers.Images.index().ajax({
        success: function(data) { reloadWindow(data); },
        error: function(data) {
            if(data.status != 401) {
                reloadWindow(data, registerAction);
            }
        }
    });
}

function checkWindowExists(id, action) {
    if(!$(id)[0]) {
        action();
    } else {
        var thisWindow = $(id);
        var task = $('#task-bar-' + thisWindow.attr('id'));
        setZIndex(thisWindow);
        if(task.hasClass('task-bar-hidden-window')) {
            thisWindow.removeClass('window-hidden');
            task.removeClass('task-bar-hidden-window');
        }
    }
    closeStartMenu();
}

function registerDblclick(id, url, registerAction) {
    $('#icon-' + id).dblclick(function(){
        checkWindowExists('#window-' + id, function(){
            url.ajax({
                success: function(data) {
                    showWindow(data);
                    if(registerAction != undefined) registerAction();
                },
                error: function(data) {
                    if(data.status != 401) {
                        showWindow(data);
                    }
                }
            });
        });
    });
}

function registerManageProductionForm(objId, productionMethod) {
    $('#manage-production-form-' + objId).submit(function(e) {
        e.preventDefault();
        var formData = new FormData($(this).get()[0]);
        var windowId = $(this).data('window-id');
        productionMethod.ajax({
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(data) {
                showWindow(data);
                closeWindow($('#' + windowId));
            },
            error: function(data) {
                console.log(data.status);
                if(data.status != 401) {
                    reloadWindow(data.responseText, function() { registerManageProductionForm(objId, productionMethod); });
                }
            }
        });
    });
}

function registerManagePageForm(objId) {
    $('#manage-page-form-' + objId).submit(function(e) {
        e.preventDefault();
        var formData = new FormData($(this).get()[0]);
        var windowId = $(this).data('window-id');
        jsRoutes.controllers.Pages.update().ajax({
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(data) {
                showWindow(data);
                closeWindow($('#' + windowId));
            },
            error: function(data) {
                if(data.status != 401) {
                    reloadWindow(data.responseText, function() { registerManagePageForm(objId); });
                }
            }
        });
    });
}

function registerManageImageForm(objId, imageMethod) {
    $('#manage-image-form-' + objId).submit(function(e) {
        e.preventDefault();
        var formData = new FormData($(this).get()[0]);
        var windowId = $(this).data('window-id');
        imageMethod.ajax({
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(data) {
                showWindow(data);
                closeWindow($('#' + windowId));
            },
            error: function(data) {
                if(data.status != 401) {
                    reloadWindow(data.responseText, function() { registerManageImageForm(objId, imageMethod); });
                }
            }
        });
    });
}

function registerManageUserForm(objId, userMethod) {
    $('#manage-user-form-' + objId).submit(function(e) {
        e.preventDefault();
        var formData = new FormData($(this).get()[0]);
        var windowId = $(this).data('window-id');
        userMethod.ajax({
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false,
            success: function(data) {
                closeWindow($('#' + windowId));
                reloadManageUser(registerManageUser);
            },
            error: function(data) {
                if(data.status != 401) {
                    reloadWindow(data.responseText, function() { registerManageUserForm(objId, userMethod); });
                }
            }
        });
    });
}

function registerManageProduction() {
    $('#window-manage-production > .main-window-content > .manage-production-item').each(function(){
        var id = $(this).attr('id');
        var iconId = id.replace('icon-', '');
        if(id == 'icon-new-production') {
            registerDblclick(iconId, jsRoutes.controllers.Productions.add(), function() {
                registerManageProductionForm('new', jsRoutes.controllers.Productions.create());
            });
        } else {
            var objId = id.replace('icon-edit-production-', '');
            registerDblclick(iconId, jsRoutes.controllers.Productions.edit(objId), function() {
                registerManageProductionForm(objId, jsRoutes.controllers.Productions.update(objId));
                $('#button-remove-production-' + objId).click(function(){
                    $.ajax({
                        url: $(this).data('url'),
                        method: 'POST',
                        success: function() {
                            closeWindow($('#window-edit-production-' + objId));
                            reloadManageProduction(registerManageProduction);
                        },
                        error: function(data) {
                            if(data.status != 401) {
                                reloadManageProduction(registerManageProduction);
                            }
                        }
                    });
                });
            });
        }
    });
}

function registerManagePage() {
    $('#window-manage-page > .main-window-content > .manage-page-item').each(function(){
        var id = $(this).attr('id');
        var iconId = id.replace('icon-', '');
        var objId = id.replace('icon-edit-page-', '');
        registerDblclick(iconId, jsRoutes.controllers.Pages.edit($(this).data('content-type')), function() {
            registerManagePageForm(objId);
        });
    });
}

function registerManageImage() {
    $('#window-manage-image > .main-window-content > .manage-image-item').each(function(){
        var id = $(this).attr('id');
        var iconId = id.replace('icon-', '');
        var objId = id.replace('icon-edit-image-', '');
        registerDblclick(iconId, jsRoutes.controllers.Images.show(objId), function(){
            $('#button-remove-image-' + objId).click(function(){
                $.ajax({
                    url: $(this).data('url'),
                    method: 'POST',
                    success: function() {
                        closeWindow($('#window-image-' + objId));
                        reloadManageImage(registerManageImage);
                    },
                    error: function(data) {
                        if(data.status != 401) {
                            reloadManageImage(registerManageImage);
                        }
                    }
                });
            });
        });
    });
}

function registerManageUser() {
    $('#window-manage-user > .main-window-content .manage-user-item').each(function(){
        var id = $(this).data('id');
        if(!id){
            registerDblclick('edit-user-new', jsRoutes.controllers.UserController.edit(), function() {
                registerManageUserForm('new', jsRoutes.controllers.UserController.create());
            });
        } else {
            registerDblclick('edit-user-' + id, jsRoutes.controllers.UserController.edit(id), function() {
                registerManageUserForm(id, jsRoutes.controllers.UserController.update(id));
            });
            $('#button-remove-user-' + id).click(function(){
                $.ajax({
                    url: $(this).data('url'),
                    method: 'POST',
                    success: function() {
                        reloadManageUser(registerManageUser);
                    },
                    error: function(data) {
                        if(data.status != 401) {
                            reloadManageUser(registerManageUser);
                        }
                    }
                });
            });
        }
    });
}


function registerWork() {
    $('#window-work > .main-window-content > .production-item').each(function(){
        var id = $(this).attr('id');
        var iconId = id.replace('icon-', '');
        registerDblclick(iconId, jsRoutes.controllers.Productions.show(id.replace('icon-production-', '')));
    });
}

function loginInfo() {
    checkWindowExists('#window-login-info', function(){
        jsRoutes.controllers.UserController.loginInfo().ajax({
            success: function(data) {
                var infoWindow = showWindow(data);
                infoWindow.resizable("destroy");
                infoWindow.find('.window-title-page').remove();
                $('#login-info-ok').click(function(){
                    closeWindow(infoWindow);
                });
            }
        });
    });
}

$(function(){
    $('.task-bar-start').click(function(){
        if($('.start-menu').hasClass('window-hidden')) {
            $(this).addClass('window-button-clicked');
            jsRoutes.controllers.UserController.control().ajax({
                method: 'GET',
                global: false,
                success: function(data) {
                    insertStartMenu(data);
                    registerDblclick('manage-production', jsRoutes.controllers.Productions.index(), registerManageProduction);
                    registerDblclick('manage-page', jsRoutes.controllers.Pages.index(), registerManagePage);
                    registerDblclick('manage-image', jsRoutes.controllers.Images.index(), registerManageImage);
                    registerDblclick('manage-user', jsRoutes.controllers.UserController.index(), registerManageUser);
                    registerLogoutForm()
                },
                error: function(data) {
                    insertStartMenu(data.responseText);
                    registerLoginForm();
                    registerLoginDemo();
                }
            });
        } else {
            closeStartMenu();
        }
    });

    $(document).click(function(e){
        if(!$('.start-menu').hasClass('window-hidden') && !$(e.target).closest('.start-menu')[0] && !$(this).hasClass('task-bar-start')) {
            closeStartMenu();
        }
    });

    $('.window').each(function(){
        $('.task-bar-content').append(taskElement($(this).attr('id'), 'bi-folder-fill', $(this).find('.window-title-text').text()));
    });

    registerDblclick('manual', jsRoutes.controllers.IntroductionController.manual());
    registerDblclick('work', jsRoutes.controllers.IntroductionController.work(), registerWork);
    registerDblclick('about', jsRoutes.controllers.IntroductionController.about());

    $('.start-menu-content.display-icon').on('selectstart', function(){
        return false;
    });

    $(document).ajaxError(function(event, jqxhr, settings, exception) {
        if(exception == "Unauthorized") {
            checkWindowExists('#window-alert-unauthorized', function(){
                jsRoutes.controllers.UserController.demoAlert().ajax({
                    success: function(data) {
                        var alertWindow = showWindow(data);
                        alertWindow.resizable("destroy");
                        alertWindow.find('.window-title-page').remove();
                        $('#alert-unauthorized-ok').click(function(){
                            closeWindow(alertWindow);
                        });
                    }
                });
            });
        }
    });

    loadWindow($('.window'));
});