<nav class="navbar navbar-expand-md navbar-dark bg-dark">
    <a class="navbar-brand" href="#">Catering is Fun</a>

    <div class="collapse navbar-collapse" id="navbarsExampleDefault">
        <ul class="navbar-nav mr-auto">
        </ul>
            
        <ul class="navbar-nav navbar-right">
            <li class="{{Request::is('Register') ? 'active':''}}">
                <h6 style="color:white">Already have account?</h6>
                <form action="{{url('/')}}" method="get">
                    <button type='submit' style="color:white" class="btn btn-outline-primary">Login</button><br>
                </form>
            </li>
        </ul>
    </div>
</nav>