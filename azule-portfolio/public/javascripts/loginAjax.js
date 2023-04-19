function closeStartMenu() {
    $('.task-bar-start').removeClass('window-button-clicked');
    $('.start-menu').empty();
    $('.start-menu').addClass('window-hidden');
}

$(function(){
    $('.task-bar-start').click(function(){
        if($('.start-menu').hasClass('window-hidden')) {
            $(this).addClass('window-button-clicked');
            jsRoutes.controllers.UserController.loginPage().ajax({
                success: function(data) {
                    var main = $('.start-menu');
                    main.empty();
                    main.append(data);
                    main.removeClass('window-hidden');
                },
                error: function(data) {
                    $('.start-menu').append(data);
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
});