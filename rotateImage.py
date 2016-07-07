import cv2
import numpy as np
from math import *
camera_matrix = np.array( [[  2467.726893, 0,  1936.02964], [0, 2473.06961, 1081.48243], [0, 0,1.0]])
dc = np.array( [ -1.53501973e-01,3.04457563e-01,8.83127622e-05,6.93998940e-04,-1.90560255e-01])

mnum=45
cap = cv2.VideoCapture('DJI_00'+str(mnum)+'.MP4')

if mnum<47:
    angle=(32.1  )
else:
    angle=(26.3)
    
#angle=0.01

ret, frame = cap.read()
gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    
cv2.imwrite(str(mnum)+'PRE.jpg',gray)

gray2 = cv2.undistort(gray,camera_matrix,dc)

cv2.imwrite(str(mnum)+'PRE2.jpg',gray)
alpha_=angle
beta_ = 90
gamma_=90

alpha = (alpha_ - 90.)*pi/180;
beta = (beta_ - 90.)*pi/180;
gamma = (gamma_ - 90.)*pi/180;
f = 12447
dist = 800

sz = gray.shape
w = sz[1]
h= sz[0]

A1 = np.array([[1, 0, -w/2],[0,1, -h/2],[0, 0,0],[0, 0,1]])

RX = np.array([[1,0,0,0],[0, cos(alpha), -sin(alpha), 0],[0,sin(alpha), cos(alpha),0],[0,0,0,1]])
RY = np.eye(4)
RZ = np.eye(4)

        #Mat RY = (Mat_<double>(4, 4) << cos(beta), 0, -sin(beta), 0, 0, 1, 0, 0, sin(beta), 0,  cos(beta), 0,                0, 0,          0, 1); 

        #Mat RZ = (Mat_<double>(4, 4) << cos(gamma), -sin(gamma), 0, 0,  sin(gamma),  cos(gamma), 0, 0,                0,          0, 1, 0,0,0,0, 1); 


R = RX #* RY * RZ; 

        
T = np.array([[1, 0, 0, 0],[0, 1, 0, 0],[0,0, 1, dist],[0, 0, 0, 1]])
A2 = np.array([[f, 0, w/2,0],[0, f, h/2, 0], [0,0,1,0]])

A2[0:3,0:3]=camera_matrix
      
M2 = np.dot(A2,np.dot(T, np.dot(R,A1)))
print(M2)
warped = cv2.warpPerspective(gray2, M2, (sz[1],sz[0]),flags=cv2.WARP_INVERSE_MAP)


cv2.imwrite(str(mnum)+'POST.jpg',warped)
cap.release()

