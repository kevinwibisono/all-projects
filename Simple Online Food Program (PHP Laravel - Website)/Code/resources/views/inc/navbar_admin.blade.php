<nav class="navbar navbar-expand-md navbar-dark bg-dark">
    <a class="navbar-brand" href="#">Catering is Fun</a>

    <h1 style = "color:white">ADMIN</h1>  

    <ul class="nav navbar-nav">
        <li class="{{Request::is('Register') ? 'active':''}}">
            <form action="{{url('/nav_admin')}}" method="post">
                {{csrf_field()}}
                <button type='submit' name='submit' value='lelang' style="color:white" class="btn btn-outline-primary">Lelang</button>
            </form>
        </li>
        <li class="{{Request::is('Register') ? 'active':''}}">
            <form action="{{url('/nav_admin')}}" method="post">
            {{csrf_field()}}
                <button type='submit' name='submit' value='merchant' style="color:white" class="btn btn-outline-primary">Data Merchant</button>
            </form>
        </li>
        <li class="{{Request::is('Register') ? 'active':''}}">
            <form action="{{url('/nav_admin')}}" method="post">
            {{csrf_field()}}
                <button style="color:white" class="btn btn-outline-primary" name='submit' value='laporan'>Data Laporan</button>
            </form>
        </li>
    </ul>

    <div class="collapse navbar-collapse" id="navbarsExampleDefault">
        <ul class="navbar-nav mr-auto">
        </ul>
        <ul class="navbar-nav navbar-right">
            <li class="{{Request::is('Register') ? 'active':''}}">
                <form action="{{url('/')}}" method="post">
                {{csrf_field()}}    
                    <button type='submit' style="color:white" class="btn btn-outline-primary">Logout</button><br>
                </form>
            </li>
        </ul>
    </div>
</nav>