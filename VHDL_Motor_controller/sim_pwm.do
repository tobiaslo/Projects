vsim -voptargs=+acc work.tb_pwm
add wave sim:/tb_pwm/tb_mclk
add wave sim:/tb_pwm/tb_reset
add wave sim:/tb_pwm/tb_duty_cycle
add wave sim:/tb_pwm/tb_pwm_signal
run 500 us
