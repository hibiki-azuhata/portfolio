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

function searchParentWindow(button) {
    return button.parents('.window')
}

function isBottom(elem) {
   return elem.scrollHeight - elem.clientHeight - elem.scrollTop <= 1;
}

function loadWindow(window) {
    window.draggable({
        handle: '.window-title'
    });
    window.mousedown(function(){
        setZIndex($(this));
    });
    window.find('.window-title').mousedown(function(){
        setZIndex($(this).parent());
    });
    window.find('.window-button-close').click(function(){
        searchParentWindow($(this)).remove();
    });
    window.find('.window-button-full-window').click(function(){
        var window = searchParentWindow($(this));
        window.css('left', 0);
        window.css('top', 0);
        window.css('width', '100%');
        window.css('height', 'calc(100% - 32px)');
    });
    window.find('.window-button-taskbar').click(function(){
        searchParentWindow($(this)).addClass('window-hidden');
    });

    $('#window-manual > .main-window-content').off('scroll');
    $('#window-manual > .main-window-content').scroll($.throttle(250, function(){
        if(!$('#window-work')[0] && isBottom(this)) {
            jsRoutes.controllers.IntroductionController.work().ajax({
                success: function(data) {
                    showWindow(data);
                },
                error: function(data) {
                    showWindow(data);
                }
            });
        }
    }));

    $('#window-work > .main-window-content').off('scroll');
    $('#window-work > .main-window-content').scroll($.throttle(250, function(){
        if(!$('#window-about')[0] && isBottom(this)) {
            jsRoutes.controllers.IntroductionController.about().ajax({
                success: function(data) {
                    showWindow(data);
                },
                error: function(data) {
                    showWindow(data);
                }
            });
        }
    }));
}

function showWindow(data) {
    var lastAddedWindow = $('.window:last-of-type');
    $('.main-windows').append(data);
    var addWindow = $('.window:last-of-type');
    addWindow.css('z-index', getMaxZ() + 1);
    if(parseInt(lastAddedWindow.css('left')) + addWindow.width() + 80 >= $(window).width() || parseInt(lastAddedWindow.css('top')) + addWindow.height() + 80 >= $(window).height()) {
        addWindow.css('top', '40px');
        addWindow.css('left', '40px');
    } else {
        addWindow.css('top', 'calc(' + lastAddedWindow.css('top')  + ' + 40px)');
        addWindow.css('left', 'calc(' + lastAddedWindow.css('left')  + ' + 40px)');
    }
    loadWindow(addWindow);
}

$(function(){
    loadWindow($('.window'));
});