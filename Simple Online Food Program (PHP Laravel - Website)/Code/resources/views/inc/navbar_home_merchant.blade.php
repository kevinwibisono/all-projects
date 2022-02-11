<nav class="navbar navbar-expand-md navbar-dark bg-dark">
    <a class="navbar-brand" href="#">Catering is Fun</a>

    <div class="collapse navbar-collapse" id="navbarsExampleDefault">

    <ul class="navbar-nav navbar-right">
            <li>
                <form action="{{url('/to_home_merchant')}}" method="post">
                    {{csrf_field()}}
                    <button type='submit' style="color:white" class="btn btn-outline-primary">Home</button><br>
                </form>
            </li>
        </ul>

      <ul class="navbar-nav navbar-right">
            <li>
                <form action="{{url('/nav_lelang')}}" method="post">
                    {{csrf_field()}}
                    <button type='submit' style="color:white" class="btn btn-outline-primary">Lelang</button><br>
                </form>
            </li>
        </ul>

        <ul class="navbar-nav mr-auto">
        </ul>

        <ul class="navbar-nav navbar-right">
            <li>
                <form action="{{url('/')}}" method="post">
                    {{csrf_field()}}
                    <button type='submit' style="color:white" class="btn btn-outline-primary">Logout</button><br>
                </form>
            </li>
        </ul>
    </div>
</nav>
