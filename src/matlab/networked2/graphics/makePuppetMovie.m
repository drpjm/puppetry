function A = makePuppetMovie(R, export, file)
%
% Outputs a movie based on the simulation of the puppet
% dynamics.
%

rightArm = R{1};
leftArm = R{2};
rightLeg = R{3};
leftLeg = R{4};


% plot puppet arms in 3D
puppet_fig = figure(9);
N = 100;
len = length(leftArm);

%A = moviein(num_frames, puppet_fig);
if(export)
    mov = avifile(file);
    mov.Quality = 25;
end

for k=1:5:len
    
    %right arm
    [XR, YR, ZR] = makeDisplayArrays(rightArm, k, 0, N, 0);
    %left arm
    [XL, YL, ZL] = makeDisplayArrays(leftArm, k, 1, N, 0);
    %right leg
    [XR_leg, YR_leg, ZR_leg] = makeDisplayArrays(rightLeg, k, 0, N, 1);
    %left leg
    [XL_leg, YL_leg, ZL_leg] = makeDisplayArrays(leftLeg, k, 1, N, 1);
    
    
    % plot results
    plot3(XR,YR,ZR,'b',XL,YL,ZL,'b',XR_leg,YR_leg,ZR_leg,'r',XL_leg,YL_leg,ZL_leg,'r');
    grid on;
    axis square;
    xlabel('x position, m');
    ylabel('y position, m');
    zlabel('z position, m');
    axis([0 0.1 -0.1 0.1 -0.16 0.1]);
    view([110 30]); 
    %view([90 0]);
    
    currFrame = getframe(puppet_fig);
    if(export)
        mov = addframe(mov,currFrame);
    end
    
%    A(:,k) = getframe(puppet_fig);
    hold off;
end

if(export)
%     movie2avi(A, '/Users/grits/Desktop/puppetMovie.avi');
    mov = close(mov);
end

