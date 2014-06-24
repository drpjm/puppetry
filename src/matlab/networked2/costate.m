function ret = costate(t, cs, t_0, x_func, dLdx, dfdx, dfda)

    % extract current state at time 't'
%     t
    x = x_func(t);
    x_traj = x(1:9);
    cs_traj = cs(1:9);
    
    % lambda depends on dLdx, dfdx, x - row vector
    dL = dLdx(x_traj);
    df = dfdx(t, x_traj);
    lam = -dL - cs_traj'*df;
    
    % mu depends on lambda, dfda, and x - scalar
    dfda_traj = dfda(t, x_traj);
    mu = cs_traj'*dfda_traj(1:9);
    
    ret = [lam'; mu];


end