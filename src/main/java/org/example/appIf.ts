@Directive ({})
selector: '[appIfRole]'
})


export class AppIfRoleDirective{
    private hasView=false;
constructor(private templageRef : TemplateRef<any>), private viewContainer: ViewContanerRef){}


@Input() set appIfRole(allowedRole: String[])

{


    const userRoles=['user', 'admin'];
    const hasPermi=userRoles.some(role=>allowedRoles.include(role))

    if(hasPermission && this.view) {
        this.viewContainer.creatEmbeddeView(this.template);
        this.hasView=true;
    }
    else if(@hasPermi &&this.hasView)

}
}

thiS.SPIsERVIEC.GETdYNAMICfIELDS().sunb
how can i g`